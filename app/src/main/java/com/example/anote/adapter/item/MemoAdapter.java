package com.example.anote.adapter.item;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anote.R;
import com.example.anote.activity.NewMemoActivity;
import com.example.anote.adapter.item.item.Memo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHolder> {
    private Activity activity;
    private ArrayList<Memo> memoList = null;
    private Realm realm;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageContents;
        TextView title;
        TextView textContents;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            imageContents = itemView.findViewById(R.id.imageContents);
            title = itemView.findViewById(R.id.title);
            textContents = itemView.findViewById(R.id.textContents);

            // update memo
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity, NewMemoActivity.class);
                    intent.putExtra("id",memoList.get(getAdapterPosition()).getId());
                    intent.putExtra("title", memoList.get(getAdapterPosition()).getTitle());
                    intent.putExtra("memoTextContents", memoList.get(getAdapterPosition()).getTextContents());
                    intent.putExtra("memoImageContents", memoList.get(getAdapterPosition()).getImageContents());
                    intent.putExtra("requestCode", "2");
                    activity.startActivityForResult(intent, 2);
                }
            });

            // remove memo
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setMessage("메모를 삭제 하시겠습니까?");
                    builder.setPositiveButton("삭제",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    removeMemo(memoList.get(getAdapterPosition()).getId());
                                    removeItemView(getAdapterPosition());
                                }
                            });
                    builder.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    builder.show();

                    return false;
                }
            });
        }
    }

    public MemoAdapter(Activity activity, ArrayList<Memo> list){
        this.activity = activity;
        this.memoList = list;
    }

    @NonNull
    @Override
    public MemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_memo, parent, false);
        MemoAdapter.ViewHolder vh = new MemoAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MemoAdapter.ViewHolder holder, int position) {
        holder.title.setText(memoList.get(position).getTitle());
        holder.textContents.setText(memoList.get(position).getTextContents());
    }

    @Override
    public int getItemCount() {
        return memoList.size();
    }

    private void removeItemView(int position) {
        memoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, memoList.size());
    }

    private void removeMemo(String id){
        realm = Realm.getDefaultInstance();
        final Memo result = realm.where(Memo.class).equalTo("id",id).findFirst();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteFromRealm(); //
            }
        });
    }
}
