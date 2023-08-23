package fr.zante.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.SelectedRestaurant;

@RunWith(MockitoJUnitRunner.class)
public class MembersRepositoryTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Mock
    private FirebaseDatabase database;

    @Mock
    private DatabaseReference ref;
    @Mock
    private DatabaseReference selectedRestaurantsRef;

    @Mock
    private DataSnapshot snapshot;

    @Mock
    private Observer<List<Member>> observer;
    @Mock
    private Observer<Member> observerMember;
    @Mock
    private Observer<List<SelectedRestaurant>> observerSelectedRestaurantList;
    @Mock
    private Observer<List<String>> observerStringList;

    private MembersRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(database.getReference("members")).thenReturn(ref);
        when(database.getReference("selectedRestaurants")).thenReturn(selectedRestaurantsRef);
        repository = MembersRepository.getInstance(database);
    }
    // *******************************
    // *********** MEMBERS ***********
    // *******************************


    // TODO NullPointerException
    @Test
    public void addMemberTest() {
        Member testMember = new Member("123", "test A", "abc@test.com", "", "", "", false);
        System.out.println("testMember = " + testMember.getName() + " repository: " + repository);
        repository.addMember(testMember);
        verify(ref).child(testMember.getName()).setValue(testMember);
    }



    // TODO OK ????
    @Test
    public void getMembersLiveDataListTest() {
        Member testMember = new Member();
        testMember.setName("Test Member");

        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            when(snapshot.getChildren()).thenReturn(Arrays.asList(snapshot));
            when(snapshot.getValue(Member.class)).thenReturn(testMember);
            valueEventListener.onDataChange(snapshot);
            return null;
        }).when(ref).addValueEventListener(any(ValueEventListener.class));
        repository.getMembersLiveDataList().observeForever(observer);
        verify(observer).onChanged(Arrays.asList(testMember));
    }



    // TODO OK ????
    @Test
    public void testGetActiveMember() {
        String memberName = "Test Member";
        Member testMember = new Member();
        testMember.setName(memberName);

        when(snapshot.getChildren()).thenReturn(Arrays.asList(snapshot));
        when(snapshot.getValue(Member.class)).thenReturn(testMember);

        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(snapshot);
            return null;
        }).when(ref).addValueEventListener(any(ValueEventListener.class));

        LiveData<Member> activeMemberLiveData = repository.getActiveMember(memberName);
        activeMemberLiveData.observeForever(observerMember);

        verify(observerMember).onChanged(testMember);
    }


    /**
     * // TODO NullPointerException
    @Test
    public void testUpdateMember() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        testMember.setSelectedRestaurantId("restaurantId");
        testMember.setSelectedRestaurantName("Restaurant Name");
        repository.updateMember(testMember);
        verify(ref.child(testMember.getName()).child("selectedRestaurantId")).setValue(testMember.getSelectedRestaurantId());
        verify(ref.child(testMember.getName()).child("selectedRestaurantName")).setValue(testMember.getSelectedRestaurantName());
    }
    */

    /**
     * // TODO NullPointerException
    @Test
    public void testUpdateNotificationsAllowed() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        testMember.setNotificationsAllowed(true);
        repository.updateNotificationsAllowed(testMember);
        verify(ref.child(testMember.getName()).child("notificationsAllowed")).setValue(testMember.isNotificationsAllowed());
    }
    */



    // **************************************
    // ****** MEMBER LIKED RESTAURANTS ******
    // **************************************
    /**
     * // TODO NullPointerException
    @Test
    public void testAddLikedRestaurant() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        String restaurantId = "restaurant123";
        repository.addLikedRestaurant(testMember, restaurantId);
        verify(ref.child(testMember.getName()).child("restaurantsLikedBy").child(restaurantId)).setValue(restaurantId);
    }
    */

    /**
     * // TODO ???
    @Test
    public void testGetActiveMemberLikedRestaurantLiveDataList() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        String id = "restaurant123";

        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            when(snapshot.getChildren()).thenReturn(Arrays.asList(snapshot));
            when(snapshot.getValue(String.class)).thenReturn(id);
            valueEventListener.onDataChange(snapshot);
            return null;
        }).when(ref.child(testMember.getName()).child("restaurantsLikedBy")).addValueEventListener(any(ValueEventListener.class));
        repository.getActiveMemberLikedRestaurantLiveDataList(testMember).observeForever(observerStringList);
        verify(observerStringList).onChanged(Arrays.asList(id));
    }
    */


    /**
     * // TODO NullPointerException
    @Test
    public void testDeleteLikedRestaurant() {
        Member testMember = new Member("123", "test A", "abc@test.com", "", "", "", false);
        String restaurantId = "restaurant123";
        repository.deleteLikedRestaurant(testMember, restaurantId);
        verify(ref.child(testMember.getName()).child("restaurantsLikedBy").child(restaurantId)).removeValue();
    }
    */

    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************
    /**
     *  // TODO NullPointerException
    @Test
    public void testAddSelectedRestaurant() {
        {
            SelectedRestaurant testSelectedRestaurant = new SelectedRestaurant("restaurant123", "restaurant123", 1);
            repository.addSelectedRestaurant(testSelectedRestaurant);
            verify(selectedRestaurantsRef.child(testSelectedRestaurant.getRestaurantId())).setValue(testSelectedRestaurant);
        }
    }
    */


    // TODO OK ????
    @Test
    public void testGetSelectedRestaurantsLiveDataList() {
        SelectedRestaurant testSelectedRestaurant = new SelectedRestaurant("restaurant123", "restaurant123", 1);

        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            when(snapshot.getChildren()).thenReturn(Arrays.asList(snapshot));
            when(snapshot.getValue(SelectedRestaurant.class)).thenReturn(testSelectedRestaurant);
            valueEventListener.onDataChange(snapshot);
            return null;
        }).when(selectedRestaurantsRef).addValueEventListener(any(ValueEventListener.class));
        repository.getSelectedRestaurantsLiveDataList().observeForever(observerSelectedRestaurantList);
        verify(observerSelectedRestaurantList).onChanged(Arrays.asList(testSelectedRestaurant));
    }


    /**
     * // TODO NullPointerException
    @Test
    public void testDeleteSelectedRestaurant() {
        String restaurantId = "restaurant123";
        repository.deleteSelectedRestaurant(restaurantId);
        verify(selectedRestaurantsRef.child(restaurantId)).removeValue();
    }
    */


    /**
     * // TODO NullPointerException
    @Test
    public void testUpdateSelectedRestaurant() {
        SelectedRestaurant testSelectedRestaurant = new SelectedRestaurant("restaurant123", "restaurant123", 5);
        repository.updateSelectedRestaurant(testSelectedRestaurant);
        verify(selectedRestaurantsRef.child(testSelectedRestaurant.getRestaurantId()).child("memberJoiningNumber"))
                .setValue(testSelectedRestaurant.getMemberJoiningNumber());
    }
    */


    // ****************************************
    // ***** SELECTED RESTAURANTS MEMBERS *****
    // ****************************************

    /**
     * // TODO NullPointerException
    @Test
    public void testAddMemberToSelectedRestaurantMemberList() {
        Member testMember = new Member();
        testMember.setMemberId("member123");
        String selectedRestaurantId = "restaurant123";
        repository.addMemberToSelectedRestaurantMemberList(testMember, selectedRestaurantId);
        verify(selectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy")
                .child(testMember.getMemberId())).setValue(testMember);
    }
    */

    /**
    @Test
    public void testGetSelectedRestaurantMemberLiveDataList() {
        // TODO ???
    }
    */

    /**
     * // TODO NullPointerException
    @Test
    public void testDeleteMemberToSelectedRestaurantMemberList() {
        Member testMember = new Member();
        testMember.setMemberId("member123");
        String selectedRestaurantId = "restaurant123";
        repository.deleteMemberToSelectedRestaurantMemberList(testMember, selectedRestaurantId);
        verify(selectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy")
                .child(testMember.getMemberId())).removeValue();
    }
    */
}
