package com.example.femi.emergent;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by user on 03/12/2017.
 */

public class DownloadService extends JobService {
    private DatabaseReference newsDatabaseReference;
    private DatabaseReference eventDatabaseReference;

    @Override
    public boolean onStartJob(JobParameters job) {
        newsDatabaseReference = FirebaseDatabase.getInstance().getReference().child("news");
        newsDatabaseReference.keepSynced(true);

        eventDatabaseReference = FirebaseDatabase.getInstance().getReference().child("event");
        eventDatabaseReference.keepSynced(true);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
