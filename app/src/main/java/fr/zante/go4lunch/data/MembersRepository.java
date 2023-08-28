package fr.zante.go4lunch.data;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.SelectedRestaurant;

public class MembersRepository {

    // Database
    FirebaseDatabase database;

    // DatabaseRef :
    DatabaseReference myRef;
    DatabaseReference mySelectedRestaurantsRef;

    private final List<Member> myMembersList = new ArrayList<>();
    private final List<String> myActiveMemberLikeList = new ArrayList<>();
    private final List<SelectedRestaurant> mySelectedRestaurantsList = new ArrayList<>();
    private final List<Member> mySelectedRestaurantMemberList = new ArrayList<>();

    private static MembersRepository repository;

    public static MembersRepository getInstance(FirebaseDatabase database) {
        if (repository == null) {
            repository = new MembersRepository(database);
        }
        return repository;
    }

    MembersRepository(FirebaseDatabase database) {
        this.database = database;
        this.myRef = database.getReference("members");
        this.mySelectedRestaurantsRef = database.getReference("selectedRestaurants");
    }

    public static void resetInstance() {
        repository = null;
    }


    // *******************************
    // *********** MEMBERS ***********
    // *******************************
    /**
     * @param member item to add in database
     * Add the new member when first login in the database
     */
    public void addMember(Member member) {
        myRef.child(member.getName()).setValue(member);
    }

    /**
     * @return the List of members in database:
     */
    public LiveData<List<Member>> getMembersLiveDataList() {
        MutableLiveData<List<Member>> membersMutableLiveData = new MutableLiveData<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myMembersList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        myMembersList.add(member);
                    }
                }
                membersMutableLiveData.setValue(myMembersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getMembersLiveData()", "onCancelled: read database failed");
            }
        });
        return membersMutableLiveData;
    }

    /**
     * @param memberName needed to find the member in the list
     * @return the Member with the given name
     */
    public LiveData<Member> getActiveMember(String memberName) {
        MutableLiveData<Member> activeMember = new MutableLiveData<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Member myActiveMember = new Member();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        if (member.getName().equals(memberName)) {
                            myActiveMember = member;
                        }
                    }
                }
                activeMember.setValue(myActiveMember);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getActiveMember()", "onCancelled: read database failed");
            }
        });
        return activeMember;
    }

    /**
     * @param member item to update in database
     * update member selected restaurant info.
     */
    public void updateMember(Member member) {
        myRef.child(member.getName()).child("selectedRestaurantId").setValue(member.getSelectedRestaurantId());
        myRef.child(member.getName()).child("selectedRestaurantName").setValue(member.getSelectedRestaurantName());
    }

    /**
     * @param member item to update in database
     * update member notifications allowed status
     */
    public void updateNotificationsAllowed(Member member) {
        myRef.child(member.getName()).child("notificationsAllowed").setValue(member.isNotificationsAllowed());
    }


    // **************************************
    // ****** MEMBER LIKED RESTAURANTS ******
    // **************************************
    /**
     * @param activeMember target whose we want to add a liked restaurant
     * @param restaurantId Id of the restaurant liked by the active member
     * Add the id of the restaurant to the active member list of liked restaurant id
     */
    public void addLikedRestaurant(Member activeMember, String restaurantId) {
        myRef.child(activeMember.getName()).child("restaurantsLikedBy").child(restaurantId).setValue(restaurantId);
    }

    /**
     * @param activeMember target whose we want the liked restaurant list
     * @return the list of ID of the restaurants liked by the member
     */
    public LiveData<List<String>> getActiveMemberLikedRestaurantLiveDataList(Member activeMember) {
        MutableLiveData<List<String>> activeMemberLikedRestaurantsMutableLiveData = new MutableLiveData<>();
        DatabaseReference myActiveMemberLikedRestaurantListRef = myRef.child(activeMember.getName()).child("restaurantsLikedBy");
        myActiveMemberLikedRestaurantListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myActiveMemberLikeList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    String id = data.getValue(String.class);
                    if (id != null) {
                        myActiveMemberLikeList.add(id);
                    }
                }
                activeMemberLikedRestaurantsMutableLiveData.setValue(myActiveMemberLikeList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getMemberLikeList()", "onCancelled: read database failed");
            }
        });
        return activeMemberLikedRestaurantsMutableLiveData;
    }

    /**
     * @param activeMember target whose we want to remove a liked restaurant from liked restaurant list
     * @param restaurantId Id of the restaurant to remove from the liked restaurant list  by the active member
     */
    public void deleteLikedRestaurant(Member activeMember, String restaurantId) {
        myRef.child(activeMember.getName()).child("restaurantsLikedBy").child(restaurantId).removeValue();
    }


    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************
    /**
     * @param selectedRestaurant to add in database
     * Add the SelectedRestaurant to the list in the database
     */
    public void addSelectedRestaurant(SelectedRestaurant selectedRestaurant) {
        mySelectedRestaurantsRef.child(selectedRestaurant.getRestaurantId()).setValue(selectedRestaurant);
    }

    /**
     * @return the List of selectedRestaurant in database:
     */
    public LiveData<List<SelectedRestaurant>> getSelectedRestaurantsLiveDataList() {
        MutableLiveData<List<SelectedRestaurant>> selectedRestaurantsMutableLiveData = new MutableLiveData<>();
        mySelectedRestaurantsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mySelectedRestaurantsList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    SelectedRestaurant selectedRestaurant = data.getValue(SelectedRestaurant.class);
                    if (selectedRestaurant != null) {
                        mySelectedRestaurantsList.add(selectedRestaurant);
                    }
                }
                selectedRestaurantsMutableLiveData.setValue(mySelectedRestaurantsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getSelectedRestList()", "onCancelled: read database failed");
            }
        });
        return selectedRestaurantsMutableLiveData;
    }

    /**
     * @param selectedRestaurantId selectedRestaurant's id to remove from database
     * Remove the SelectedRestaurant from the list in database
     */
    public void deleteSelectedRestaurant(String selectedRestaurantId) {
        mySelectedRestaurantsRef.child(selectedRestaurantId).removeValue();
    }

    /**
     * @param selectedRestaurant item we want to update
     * Update the value of the selected restaurant memberJoiningNumber in database
     */
    public void updateSelectedRestaurant(SelectedRestaurant selectedRestaurant) {
        mySelectedRestaurantsRef.child(selectedRestaurant.getRestaurantId()).child("memberJoiningNumber").setValue(selectedRestaurant.getMemberJoiningNumber());
    }


    // ****************************************
    // ***** SELECTED RESTAURANTS MEMBERS *****
    // ****************************************

    /**
     * @param activeMember item to add
     * @param selectedRestaurantId targeted restaurant id we want to add a member
     * add to the targeted restaurant member list a member
     */
    public void addMemberToSelectedRestaurantMemberList(Member activeMember, String selectedRestaurantId) {
        mySelectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy").child(activeMember.getMemberId()).setValue(activeMember);
    }

    /**
     * @param selectedRestaurantId targeted restaurant id we want member list from
     * @return the selected restaurant member list
     */
    public LiveData<List<Member>> getSelectedRestaurantMemberLiveDataList(String selectedRestaurantId) {
        MutableLiveData<List<Member>> selectedRestaurantMembersMutableLiveData = new MutableLiveData<>();
        DatabaseReference mySelectedRestaurantMemberListRef = mySelectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy");
        mySelectedRestaurantMemberListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mySelectedRestaurantMemberList.clear();
                for (DataSnapshot data: snapshot.getChildren()) {
                    Member member = data.getValue(Member.class);
                    if (member != null) {
                        mySelectedRestaurantMemberList.add(member);
                    }
                }
                selectedRestaurantMembersMutableLiveData.setValue(mySelectedRestaurantMemberList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("getSelectRestMemberList", "onCancelled: read database failed");
            }
        });
        return selectedRestaurantMembersMutableLiveData;
    }

    /**
     * @param activeMember item to remove
     * @param selectedRestaurantId targeted restaurant id we want to remove a member
     * remove from the targeted restaurant member list a member
     */
    public void deleteMemberToSelectedRestaurantMemberList(Member activeMember, String selectedRestaurantId) {
        mySelectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy").child(activeMember.getMemberId()).removeValue();
    }

}
