package fr.zante.go4lunch.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

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

    // Singleton:
    public synchronized static MemberRepository getInstance() {
        if (repository == null) {
            repository = new MemberRepository();
        }
        return repository;
    }

    // Build the list with Data:
    public void updateData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clean the list:
                membersList.clear();
                // build a new one:
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        membersList.add(member);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MemberRepository.update", "onCancelled: read database failed");
            }
        });
    }

    // Return the list:
    public List<Member> getMembersList() {
        return membersList;
    }

    // get a Member by his Id:
    public Member getMemberById(String userId) {
        for (int i=0; i<membersList.size(); i++) {
            if (membersList.get(i).getMemberId().equals(userId)) {
                return membersList.get(i);
            }
        }
        return null;
    }

    // Add a Member in Database:
    public void addMember(Member member) {
        myRef.child(member.getName()).setValue(member);
    }

    // Update the member SelectedRestaurant:
    public void updateMemberSelectedRestaurant(String userId, String selectedRestaurantId) {
        Member memberToUpdate = getMemberById(userId);
        memberToUpdate.setSelectedRestaurantId(selectedRestaurantId);
        myRef.child(memberToUpdate.getName()).setValue(memberToUpdate);
    }

}
