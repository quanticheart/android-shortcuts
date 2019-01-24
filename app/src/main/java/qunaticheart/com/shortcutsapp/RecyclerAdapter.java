/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcutsapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import qunaticheart.com.shortcut.ShortcutHelper;
import qunaticheart.com.shortcut.WebsiteUtils;

@TargetApi(Build.VERSION_CODES.N_MR1)
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<ShortcutInfo> mList;
    private final LayoutInflater mInflater;

    RecyclerAdapter(Context context, List<ShortcutInfo> mList) {
        this.context = context;
        this.mList = mList;
        mInflater = context.getSystemService(LayoutInflater.class);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShortcutHolder(mInflater.inflate(R.layout.list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (viewHolder instanceof ShortcutHolder) {
            ShortcutHolder holder = (ShortcutHolder) viewHolder;
            holder.bindView(context, mList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    void refreshList(List<ShortcutInfo> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public class ShortcutHolder extends RecyclerView.ViewHolder {
        View view;

        ShortcutHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        private void bindView(Context context, final ShortcutInfo shortcut) {
            view.setTag(shortcut);

            ImageView img = view.findViewById(R.id.icon);

            final Bitmap bitmap = WebsiteUtils.fetchFavicon(Uri.parse(WebsiteUtils.normalizeUrl(shortcut.getId())));

            final Bitmap recipe;
            if (bitmap == null)
                recipe = BitmapFactory.decodeResource(context.getResources(), qunaticheart.com.shortcut.R.drawable.link);
            else
                recipe = bitmap;

            img.setImageBitmap(recipe);

            final TextView line1 = view.findViewById(R.id.line1);
            final TextView line2 = view.findViewById(R.id.line2);

            line1.setText(shortcut.getLongLabel());
            line2.setText(ShortcutHelper.getType(shortcut));

            final Button home = view.findViewById(R.id.home);
            final Button remove = view.findViewById(R.id.remove);
            final Button disable = view.findViewById(R.id.disable);

            disable.setText(shortcut.isEnabled() ? R.string.disable_shortcut : R.string.enable_shortcut);
            home.setVisibility(shortcut.isEnabled() ? View.VISIBLE : View.GONE);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.addOnHomeScreen(shortcut, recipe);
                }
            });

            remove.setVisibility(shortcut.isDynamic() ? View.VISIBLE : View.GONE);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.removeShortcut(shortcut);
                }
            });
            disable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.disableOrEnableShortcut(shortcut);
                }
            });
        }
    }

    //==============================================================================================
    //
    // ** Interface ClickLisnner
    //
    //==============================================================================================

    private OnclickListener listener;

    void setOnClickListener(OnclickListener onclickListener) {
        listener = onclickListener;
    }

    public interface OnclickListener {
        void removeShortcut(ShortcutInfo shortcut);

        void disableOrEnableShortcut(ShortcutInfo shortcut);

        void addOnHomeScreen(ShortcutInfo shortcut, Bitmap icon);
    }

    //==============================================================================================
    //
    // ** Download Bitmap
    //
    //==============================================================================================

}
