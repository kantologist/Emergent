package Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.femi.emergent.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Adapters.ContactAdapter;
import Models.Contact;
import Utils.Utils;

import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class ContactFragment extends Fragment {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ContactAdapter contactAdapter;
    List<Contact> contacts;
    static final int REQUEST_TAKE_PHOTO = 1;
    String CURRENT_EMAIL;
    // utilities object
    Utils util = new Utils();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
//        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv);
//        Contact[] contactList = new Contact[] {
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//                new Contact(1, "azeezfemi17937@yahoo.com", 14),
//        };
//        contacts = new ArrayList<Contact>(Arrays.asList(contactList));
//        contactAdapter = new ContactAdapter(getActivity(), contacts);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(contactAdapter);
//        linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.addOnItemTouchListener(new ContactAdapter.RecyclerTouchListener(getActivity(), recyclerView, new ContactAdapter.ClickListener(){

//            @Override
//            public void onClick(View view, int position) throws ClassNotFoundException {
//                CURRENT_EMAIL = contacts.get(position).getName();
//                startActivityForResult(util.dispatchTakePictureIntent(getContext()), REQUEST_TAKE_PHOTO);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));
        return rootView;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//       if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//           Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Uri.encode(CURRENT_EMAIL)));
////           intent.setType("text/plain");
////           intent.putExtra(Intent.EXTRA_EMAIL, CURRENT_EMAIL);
//           intent.putExtra(Intent.EXTRA_SUBJECT, "Emergency");
//           intent.putExtra(Intent.EXTRA_TEXT, "There is an emergency situation going on please help");
//           Toast.makeText(getContext(), "There are clients "+ util.getCurrentPhotoPath(), Toast.LENGTH_SHORT).show();
//           Uri uri = Uri.fromFile(new File(util.getCurrentPhotoPath()));
//           intent.putExtra(Intent.EXTRA_STREAM, uri);
//           try{
//               startActivity(Intent.createChooser(intent, "Send email..."));
//           } catch (android.content.ActivityNotFoundException ex){
//               Toast.makeText(getContext(), "There are no email clients", Toast.LENGTH_SHORT).show();
//           }
//
//       }
//    }
}
