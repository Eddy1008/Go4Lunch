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
    private Member activeMember;
    private List<String> myActiveMemberRestaurantLikedList = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("members");

    // Singleton:
    public synchronized static MemberRepository getInstance() {
        if (repository == null) {
            repository = new MemberRepository();
        }
        return repository;
    }

    // Build Data:
    public void updateData(String userId) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clean the list:
                membersList.clear();
                // build membersList:
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        membersList.add(member);
                    }
                }
                // get activeMember:
                activeMember = getActiveMember(userId);
                // build activeMember restaurant liked list:
                getMyActiveMemberRestaurantLikedList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MemberRepository.update", "onCancelled: read database failed");
            }
        });
    }

    // Build the list of restaurant liked by activeMember:
    public void getMyActiveMemberRestaurantLikedList() {
        DatabaseReference myMemberRestaurantLikedListRef = myRef.child(activeMember.getName()).child("restaurantsLikedId");
        myMemberRestaurantLikedListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clean the list:
                myActiveMemberRestaurantLikedList.clear();
                // build myActiveMemberRestaurantLikedList:
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getValue(String.class);
                    if (id != null) {
                        myActiveMemberRestaurantLikedList.add(id);
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

    // Return the list:
    public List<String> getActiveMemberRestaurantLikedList() {
        return myActiveMemberRestaurantLikedList;
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

    public Member getActiveMember(String activeUserId) {
        activeMember = getMemberById(activeUserId);
        return activeMember;
    }

    // Add a Member in Database:
    public void addMember(Member member) {
        myRef.child(member.getName()).setValue(member);
    }

    // Update the member SelectedRestaurant:
    public void updateMemberSelectedRestaurant(String selectedRestaurantId, String selectedRestaurantName) {
        activeMember.setSelectedRestaurantId(selectedRestaurantId);
        activeMember.setSelectedRestaurantName(selectedRestaurantName);
        myRef.child(activeMember.getName()).child("selectedRestaurantId").setValue(selectedRestaurantId);
        myRef.child(activeMember.getName()).child("selectedRestaurantName").setValue(selectedRestaurantName);
    }

    public void updateMemberRestaurantLikedList(String restaurantLikedId) {
        boolean isLiked = false;
        for (int i = 0; i< myActiveMemberRestaurantLikedList.size(); i++) {
            if (myActiveMemberRestaurantLikedList.get(i).equals(restaurantLikedId)) {
                isLiked = true;
                break;
            }
        }

        if (isLiked) {
            myRef.child(activeMember.getName()).child("restaurantsLikedId").child(restaurantLikedId).removeValue();
        } else {
            myRef.child(activeMember.getName()).child("restaurantsLikedId").child(restaurantLikedId).setValue(restaurantLikedId);
        }
    }

    public boolean isLikedRestaurant(String restaurantId) {
        boolean isLikedRestaurant = false;
        for (int i=0; i<myActiveMemberRestaurantLikedList.size(); i++) {
            if (myActiveMemberRestaurantLikedList.get(i).equals(restaurantId)) {
                isLikedRestaurant = true;
                break;
            }
        }
        return isLikedRestaurant;
    }
}
