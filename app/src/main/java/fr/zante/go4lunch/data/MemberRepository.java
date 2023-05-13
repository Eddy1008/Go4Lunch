package fr.zante.go4lunch.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.model.Member;

public class MemberRepository {

    private static MemberRepository repository;
    private List<Member> membersList = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("members");

    public synchronized static MemberRepository getInstance() {
        if (repository == null) {
            repository = new MemberRepository();
        }
        return repository;
    }

    public void updateData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get the list
                membersList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);

                    if (member != null) {
                        membersList.add(member);
                    } else {
                        Log.d("MemberRepository", "onDataChange: member Null !!!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MemberRepository", "onCancelled: read database failed");
            }
        });
    }

    public List<Member> getMembersList() {
        return membersList;
    }

    public Member getMemberById(String userId) {
        for (int i=0; i<membersList.size(); i++) {
            if (membersList.get(i).getMemberId().equals(userId)) {
                return membersList.get(i);
            }
        }
        return null;
    }

    // TODO à corriger ! remplace la donnée actuelle au lieu d'en ajouter !!
    public void addMember(Member member) {
        myRef.child(member.getMemberId()).setValue(member);
    }


}
