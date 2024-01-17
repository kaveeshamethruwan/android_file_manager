package com.kaviz.filemanager.Adapters;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kaviz.filemanager.Interfaces.InternalClickEvents;
import com.kaviz.filemanager.R;
import com.kaviz.filemanager.databinding.FileContainerBinding;
import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private Context context;
    private List<File>fileList;
    private InternalClickEvents clickEvents;

    public FileAdapter(Context context, List<File> fileList, InternalClickEvents clickEvents) {
        this.context = context;
        this.fileList = fileList;
        this.clickEvents = clickEvents;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.file_container, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {

        File file = fileList.get(position);

        holder.binding.fileNameText.setText(file.getName());
        holder.binding.fileNameText.setSelected(true);

         if (file.isDirectory()) {

             int items = 0;
             File [] files = file.listFiles();
             assert files != null;
             for (File singleFile : files) {

                 if (!singleFile.isHidden()) {

                     items++;

                 }

             }

             String itemsText = items + " Files";
             holder.binding.fileSizeText.setText(itemsText);

         } else {

             String fileSize = Formatter .formatShortFileSize(context, file.length());
             holder.binding.fileSizeText.setText(fileSize);

         }

         String fileName = file.getName().toLowerCase();

         if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") || fileName.endsWith(".png")) {

             holder.binding.fileIcon.setImageResource(R.drawable.image);

         } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wav")) {

             holder.binding.fileIcon.setImageResource(R.drawable.audio);

         } else if (fileName.endsWith(".apk")) {

             holder.binding.fileIcon.setImageResource(R.drawable.apk);

         } else if (fileName.endsWith(".mp4")) {

             holder.binding.fileIcon.setImageResource(R.drawable.video);

         } else if (fileName.endsWith(".pdf") || fileName.endsWith(".doc")) {

             holder.binding.fileIcon.setImageResource(R.drawable.document);

         } else {

             holder.binding.fileIcon.setImageResource(R.drawable.folder);

         }

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        FileContainerBinding binding;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = FileContainerBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickEvents.onFileClick(getAdapterPosition());

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    clickEvents.onFileLongClick(getAdapterPosition());

                    return true;
                }
            });

        }
    }

}
