package com.kaviz.filemanager.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kaviz.filemanager.Adapters.FileAdapter;
import com.kaviz.filemanager.FileOpener;
import com.kaviz.filemanager.Interfaces.InternalClickEvents;
import com.kaviz.filemanager.R;
import com.kaviz.filemanager.databinding.MenuDesignLayoutBinding;

import java.io.File;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CategorizedFragment extends Fragment implements InternalClickEvents {

    private View view;
    private RecyclerView categorizedRecyclerView;
    private List<File>fileList;
    private FileAdapter adapter;
    private File storage;
    private String data;
    private String fileType;
    private String [] items = {"Details", "Rename", "Delete", "Share"};

    private File path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_categorized, container, false);

        categorizedRecyclerView = view.findViewById(R.id.categorizedRecyclerView);

        fileList = new ArrayList<>();
        adapter = new FileAdapter(getContext(), fileList, this);
        categorizedRecyclerView.setHasFixedSize(true);
        categorizedRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categorizedRecyclerView.setAdapter(adapter);

        Bundle bundle = this.getArguments();

        if (bundle != null) {

            fileType = bundle.getString("fileType");

            if (fileType.equals("downloads")) {

                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                System.out.println(path);

            } else {

                path = Environment.getExternalStorageDirectory();

            }

        }

        runTimePermission();

        return view;

    }

    private void runTimePermission() {

        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                displayFiles(multiplePermissionsReport);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                permissionToken.continuePermissionRequest();

            }

        }).check();

    }

    private ArrayList<File>findFiles(File file) {

        ArrayList<File>fileArrayList = new ArrayList<>();
        File [] files = file.listFiles();

        if (files != null) {

            for (File singleFile: files) {

                if (singleFile.isDirectory() && !singleFile.isHidden()) {

                    fileArrayList.addAll(findFiles(singleFile));

                } else {

                    switch (fileType) {

                        case "image":
                            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png")) {

                                fileArrayList.add(singleFile);

                            }

                        break;

                        case "video":
                            if (singleFile.getName().toLowerCase().endsWith(".mp4")) {

                                fileArrayList.add(singleFile);

                            }

                            break;

                        case "music":
                            if (singleFile.getName().toLowerCase().endsWith(".mp3") || singleFile.getName().toLowerCase().endsWith(".wav")) {

                                fileArrayList.add(singleFile);

                            }

                            break;

                        case "docs":
                            if (singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc")) {

                                fileArrayList.add(singleFile);

                            }

                            break;

                        case "apk":
                            if (singleFile.getName().toLowerCase().endsWith(".apk")) {

                                fileArrayList.add(singleFile);

                            }

                            break;

                        case "downloads":
                            if (singleFile.getName().toLowerCase().endsWith(".jpeg") || singleFile.getName().toLowerCase().endsWith(".apk") ||
                                    singleFile.getName().toLowerCase().endsWith(".jpg") || singleFile.getName().toLowerCase().endsWith(".png") || singleFile.getName().toLowerCase().endsWith(".mp3")
                                    || singleFile.getName().toLowerCase().endsWith(".wav") || singleFile.getName().toLowerCase().endsWith(".mp4")
                                    || singleFile.getName().toLowerCase().endsWith(".pdf") || singleFile.getName().toLowerCase().endsWith(".doc")) {

                                fileArrayList.add(singleFile);

                            }

                            break;

                    }


                }

            }

        }

        return fileArrayList;

    }

    private void displayFiles(MultiplePermissionsReport multiplePermissionsReport) {

        fileList.addAll(findFiles(path));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onFileClick(int position) {

        File file = fileList.get(position);

        if (file.isDirectory()) {

            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());

            CategorizedFragment internalStorageFragment = new CategorizedFragment();
            internalStorageFragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, internalStorageFragment).addToBackStack(null).commit();

        } else {

            try {

                //to open file with event
                Intent intent = FileOpener.openFile(requireContext().getApplicationContext(), file);
                startActivity(intent);

            } catch (Exception e) {

                e.printStackTrace();

            }


        }

    }

    @Override
    public void onFileLongClick(int position) {

        File file = fileList.get(position);

        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.option_dialog);
        dialog.setTitle("Select Options");

        ListView listView = dialog.findViewById(R.id.menuList);
        CustomAdapter customAdapter = new CustomAdapter(requireContext(), items);
        listView.setAdapter(customAdapter);

        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                onFileLongClickProcess(file, adapterView.getItemAtPosition(i).toString(), position);
                dialog.cancel();

            }
        });

    }

    private void onFileLongClickProcess(File file, String menuItem, int position) {


        if (menuItem.equals("Details")) {

            filesDetails(file);
            System.out.println(items[0]);

        } else if (menuItem.equals(items[1])) {

            fileRename(file, position);
            System.out.println(items[1]);

        } else if (menuItem.equals(items[2])) {

            deleteFile(file, position);
            System.out.println(items[2]);

        } else {

            System.out.println(items[3]);
            shareFile(file);

        }

    }

    private void shareFile(File file) {

        if (!file.isDirectory()) {

            String fileName = file.getName().substring(0, file.getName().indexOf("."));

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file.getAbsolutePath()));

            //if you need
            //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
            startActivity(Intent.createChooser(intentShareFile, "Share "+fileName));

        } else {

            Toast.makeText(requireContext(), "Cannot share a folder!", Toast.LENGTH_SHORT).show();

        }

    }

    private void deleteFile(File file, int position) {

        String fileName = file.getName().substring(0, file.getName().indexOf("."));

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(fileName);
        dialog.setMessage("Are you sure you want to delete this \""+fileName+"\" file?");

        dialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (file.delete()) {

                    fileList.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(requireContext(), "\""+fileName+" File Deleted Successfully!\"", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(requireContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                }

            }
        });

        dialog.setPositiveButton("No", null);

        dialog.show();

    }

    private void fileRename(File file, int position) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
        dialog.setTitle(items[1]);

        EditText renameText = new EditText(requireContext());
        dialog.setView(renameText);
        renameText.setText(file.getName().substring(0, file.getName().indexOf(".")));

        dialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String newFileName = renameText.getText().toString();

                if (newFileName.isEmpty()) {

                    Toast.makeText(requireContext(), "New File Name Required", Toast.LENGTH_SHORT).show();

                } else {

                    String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                    System.out.println("extension : "+extension);
                    File currentFile = new File(file.getAbsolutePath());
                    File destinationFile = new File(file.getAbsolutePath().replace(file.getName(), newFileName+extension));

                    if (currentFile.renameTo(destinationFile)) {

                        fileList.set(position, destinationFile);
                        adapter.notifyItemChanged(position);
                        Toast.makeText(requireContext(), "File Renamed Successfully!", Toast.LENGTH_SHORT).show();


                    } else {

                        Toast.makeText(requireContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                    }

                    dialogInterface.cancel();

                }

            }
        });

        dialog.setNegativeButton("Cancel", null);
        dialog.show();

    }

    private void filesDetails(File file) {

        AlertDialog.Builder detailDialog = new AlertDialog.Builder(requireContext());
        detailDialog.setTitle(items[0]);
        //final TextView detailsText = new TextView(requireContext());
        //detailDialog.setView(detailsText);

        Date lastModified = new Date(file.lastModified());
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = format.format(lastModified);

        String details = "File Name : "+file.getName()+" \n"+"Size : "+
                Formatter.formatShortFileSize(requireContext(), file.length())+"\n"+"File Path : "+
                file.getAbsolutePath()+"\n"+"Last Modified : "+formattedDate;

        detailDialog.setMessage(details);

        //detailsText.setText(details);
        detailDialog.setNegativeButton("OK", null);
        detailDialog.show();

    }

    class CustomAdapter extends ArrayAdapter<String> {

        private Context context;
        private String [] menuList;

        public CustomAdapter(@NonNull Context context, String [] menuList) {
            super(context, R.layout.menu_design_layout, R.id.optionsText, menuList);

            this.context = context;
            this.menuList = menuList;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View viewObject = getLayoutInflater().inflate(R.layout.menu_design_layout, null);

            String menuItem = menuList[position];

            MenuDesignLayoutBinding binding = MenuDesignLayoutBinding.bind(viewObject);
            binding.optionsText.setText(menuItem);
            //{"Details", "Rename", "Delete", "Share"};

            switch (menuItem) {
                case "Details":
                    binding.imageOption.setImageResource(R.drawable.details);
                    break;

                case "Rename":
                    binding.imageOption.setImageResource(R.drawable.edit);
                    break;

                case "Delete":
                    binding.imageOption.setImageResource(R.drawable.delete);
                    break;

                default:
                    binding.imageOption.setImageResource(R.drawable.share);
                    break;
            }

            return viewObject;

        }
    }

}