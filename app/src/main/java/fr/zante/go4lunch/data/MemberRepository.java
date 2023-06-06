package fr.zante.go4lunch.data;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.SelectedRestaurant;

public class MemberRepository {

    private static MemberRepository repository;

    // Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // DatabaseRef :
    DatabaseReference myRef = database.getReference("members");
    DatabaseReference mySelectedRestaurantsRef = database.getReference("selectedRestaurants");

    // DATA Members
    private List<Member> membersList = new ArrayList<>();

    // DATA ActiveMember:
    private Member activeMember;
    private List<String> myActiveMemberRestaurantLikedList = new ArrayList<>();

    // DATA SelectedRestaurants
    private List<SelectedRestaurant> selectedRestaurantsList = new ArrayList<>();

    // DATA selected Restaurant Member List
    private List<String> mySelectedRestaurantMemberList = new ArrayList<>();

    // Singleton:
    public synchronized static MemberRepository getInstance() {
        if (repository == null) {
            repository = new MemberRepository();
        }
        return repository;
    }

    // Add a Member in Database:
    public void addMember(Member member) {
        myRef.child(member.getName()).setValue(member);
    }

    // get from database membersList, set activeMember, get from database activeMember LikedRestaurantList, get from database SelectedRestaurantList
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
                // build selected restaurant list:
                getSelectedRestaurantsListData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Return the members list:
    public List<Member> getMembersList() {
        return membersList;
    }

    // create and update active member restaurant liked list:
    public void updateMemberRestaurantLikedList(String restaurantLikedId) {
        boolean isLiked = isLikedRestaurant(restaurantLikedId);
        if (isLiked) {
            myRef.child(activeMember.getName()).child("restaurantsLikedId").child(restaurantLikedId).removeValue();
        } else {
            myRef.child(activeMember.getName()).child("restaurantsLikedId").child(restaurantLikedId).setValue(restaurantLikedId);
        }
    }

    // check if restaurant is already liked
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

    public boolean isMySelectedRestaurant(String restaurantId) {
        if(restaurantId.equals(activeMember.getSelectedRestaurantId())) {
            return true;
        } else {
            return false;
        }
    }

    // get the list of restaurant liked by activeMember from database:
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

            }
        });
    }

    // Return the active member liked restaurants list:
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

    // get the active member:
    public Member getActiveMember(String activeUserId) {
        activeMember = getMemberById(activeUserId);
        return activeMember;
    }

    // get selected restaurant List from database:
    public void getSelectedRestaurantsListData() {
        mySelectedRestaurantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clean the list
                selectedRestaurantsList.clear();
                // build the list displayed:
                for (DataSnapshot data: snapshot.getChildren()) {
                    SelectedRestaurant selectedRestaurant = data.getValue(SelectedRestaurant.class);
                    if (selectedRestaurant != null) {
                        selectedRestaurantsList.add(selectedRestaurant);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Return the selected restaurant list:
    public List<SelectedRestaurant> getSelectedRestaurantsList() { return selectedRestaurantsList; }

    // Add a selectedRestaurant in database:
    public void addSelectedRestaurant(SelectedRestaurant selectedRestaurant) {
        mySelectedRestaurantsRef.child(selectedRestaurant.getRestaurantId()).setValue(selectedRestaurant);
    }

    // Verifie si le restaurant est deja selectionné et existe dans la liste en bdd:
    public boolean isInMySelectedRestaurantList(String restaurantId) {
        for (int i=0; i<selectedRestaurantsList.size(); i++) {
            if (selectedRestaurantsList.get(i).getRestaurantId().equals(restaurantId)) {
                return true;
            }
        }
        return false;
    }

    // Recupere la liste des membres ayant selectionné ce restaurant:
    public void getSelectedRestaurantMemberList(String restaurantId) {
        DatabaseReference mySelectedRestaurantMemberListRef = mySelectedRestaurantsRef.child(restaurantId).child("restaurantSelectedBy");
        mySelectedRestaurantMemberListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mySelectedRestaurantMemberList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String memberName = data.getValue(String.class);
                    if (memberName != null) {
                        mySelectedRestaurantMemberList.add(memberName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public List<String> getMySelectedRestaurantMemberList() { return mySelectedRestaurantMemberList; }

    public void addMemberSelectedRestaurant(String selectedRestaurantId, String selectedRestaurantName) {
        if (!isInMySelectedRestaurantList(selectedRestaurantId)) {
            SelectedRestaurant mySelectedRestaurant = new SelectedRestaurant(selectedRestaurantId, selectedRestaurantName);
            selectedRestaurantsList.add(mySelectedRestaurant);
            addSelectedRestaurant(mySelectedRestaurant);
        }
        mySelectedRestaurantMemberList.add(activeMember.getName());
        addMemberToSelectedRestaurantMemberList(selectedRestaurantId);

        activeMember.setSelectedRestaurantId(selectedRestaurantId);
        activeMember.setSelectedRestaurantName(selectedRestaurantName);
        myRef.child(activeMember.getName()).child("selectedRestaurantId").setValue(selectedRestaurantId);
        myRef.child(activeMember.getName()).child("selectedRestaurantName").setValue(selectedRestaurantName);
    }
    public void deleteMemberSelectedRestaurant() {
        for (int i=0; i<mySelectedRestaurantMemberList.size(); i++) {
            if (mySelectedRestaurantMemberList.get(i).equals(activeMember.getName())) {
                mySelectedRestaurantMemberList.remove(i);
            }
        }
        mySelectedRestaurantsRef.child(activeMember.getSelectedRestaurantId()).child("restaurantSelectedBy").child(activeMember.getMemberId()).removeValue();
        if (mySelectedRestaurantMemberList.size() == 0) {
            for (int i=0; i<selectedRestaurantsList.size(); i++) {
                if (selectedRestaurantsList.get(i).getRestaurantId().equals(activeMember.getSelectedRestaurantId())) {
                    selectedRestaurantsList.remove(selectedRestaurantsList.get(i));
                }
            }
            mySelectedRestaurantsRef.child(activeMember.getSelectedRestaurantId()).removeValue();
        }
    }

    // Update the member SelectedRestaurant:
    public void updateMemberSelectedRestaurant(String selectedRestaurantId, String selectedRestaurantName) {
        if (activeMember.getSelectedRestaurantId().equals("")) {
            // Ajoute L'élément:
            addMemberSelectedRestaurant(selectedRestaurantId, selectedRestaurantName);
        } else {
            if (activeMember.getSelectedRestaurantId().equals(selectedRestaurantId)) {
                // Supprime l'élément:
                deleteMemberSelectedRestaurant();
                activeMember.setSelectedRestaurantId("");
                activeMember.setSelectedRestaurantName("");
                myRef.child(activeMember.getName()).child("selectedRestaurantId").setValue("");
                myRef.child(activeMember.getName()).child("selectedRestaurantName").setValue("");
            } else {
                // Supprime l'élément:
                deleteMemberSelectedRestaurant();
                //Ajoute L'élément:
                addMemberSelectedRestaurant(selectedRestaurantId, selectedRestaurantName);
            }
        }
    }
    void addMemberToSelectedRestaurantMemberList(String restaurantId) {
        mySelectedRestaurantsRef.child(restaurantId).child("restaurantSelectedBy").child(activeMember.getMemberId()).setValue(activeMember.getName());
    }
}
