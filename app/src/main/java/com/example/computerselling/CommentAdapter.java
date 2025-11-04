package com.example.computerselling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentsList;

    public CommentAdapter(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        holder.tvUserName.setText(comment.getUsername());
        holder.tvCommentContent.setText(comment.getContent());

        // Sử dụng phương thức định dạng thời gian (ví dụ: "Vừa xong")
        holder.tvTimestamp.setText(comment.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // Phương thức cần thiết để cập nhật danh sách (được gọi từ Activity)
    public void addComment(Comment comment) {
        commentsList.add(0, comment); // Thêm vào vị trí đầu tiên
        notifyItemInserted(0); // Chỉ cập nhật item mới
    }

    // Phương thức để thay thế toàn bộ danh sách (khi tải ban đầu)
    public void setComments(List<Comment> newComments) {
        this.commentsList.clear();
        this.commentsList.addAll(newComments);
        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvCommentContent, tvTimestamp;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_username);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}