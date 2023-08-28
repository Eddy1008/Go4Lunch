package fr.zante.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

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

    @After
    public void tearDown() {
        MembersRepository.resetInstance();
    }

    // *******************************
    // *********** MEMBERS ***********
    // *******************************
    @Test
    public void addMemberTest() {
        Member testMember = new Member("123", "test A", "abc@test.com", "", "", "", false);
        System.out.println("testMember = " + testMember.getName() + " repository: " + repository);
        DatabaseReference childRef = mock();
        when(ref.child(anyString())).thenReturn(childRef);
        repository.addMember(testMember);
        verify(ref).child(testMember.getName());
        verify(childRef).setValue(testMember);
    }

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
        //verify(observer).onChanged(Arrays.asList(testMember));

        ArgumentCaptor<List<Member>> captor = ArgumentCaptor.forClass(List.class);
        verify(observer).onChanged(captor.capture());
        List<Member> actualMembers = captor.getValue();
        assertEquals(testMember.getName(), actualMembers.get(0).getName());
        assertEquals(actualMembers.size(), 1);
    }

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


    @Test
    public void testUpdateMember() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        testMember.setSelectedRestaurantId("restaurantId");
        testMember.setSelectedRestaurantName("Restaurant Name");
        DatabaseReference childRefId = mock();
        DatabaseReference childRefName = mock();
        DatabaseReference childRefMember = mock();
        when(ref.child("Test Member")).thenReturn(childRefMember);
        when(childRefMember.child("selectedRestaurantId")).thenReturn(childRefId);
        when(childRefMember.child("selectedRestaurantName")).thenReturn(childRefName);
        repository.updateMember(testMember);
        verify(ref.child(testMember.getName()).child("selectedRestaurantId")).setValue(testMember.getSelectedRestaurantId());
        verify(ref.child(testMember.getName()).child("selectedRestaurantName")).setValue(testMember.getSelectedRestaurantName());
    }

    @Test
    public void testUpdateNotificationsAllowed() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        testMember.setNotificationsAllowed(true);


        DatabaseReference childRefMember = mock();
        DatabaseReference childRefNotifications = mock();
        when(ref.child(testMember.getName())).thenReturn(childRefMember);
        when(childRefMember.child("notificationsAllowed")).thenReturn(childRefNotifications);

        repository.updateNotificationsAllowed(testMember);
        verify(ref.child(testMember.getName()).child("notificationsAllowed")).setValue(testMember.isNotificationsAllowed());
    }



    // **************************************
    // ****** MEMBER LIKED RESTAURANTS ******
    // **************************************
    @Test
    public void testAddLikedRestaurant() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        String restaurantId = "restaurant123";

        DatabaseReference childRefMember = mock();
        DatabaseReference childRefMemberRestaurantsLikedBy = mock();
        DatabaseReference childRefMemberRestaurantId = mock();
        when(ref.child(testMember.getName())).thenReturn(childRefMember);
        when(childRefMember.child("restaurantsLikedBy")).thenReturn(childRefMemberRestaurantsLikedBy);
        when(childRefMemberRestaurantsLikedBy.child(restaurantId)).thenReturn(childRefMemberRestaurantId);

        repository.addLikedRestaurant(testMember, restaurantId);
        verify(ref.child(testMember.getName()).child("restaurantsLikedBy").child(restaurantId)).setValue(restaurantId);
    }

    @Test
    public void testGetActiveMemberLikedRestaurantLiveDataList() {
        Member testMember = new Member();
        testMember.setName("Test Member");
        String id = "restaurant123";

        when(snapshot.getChildren()).thenReturn(Arrays.asList(snapshot));
        when(snapshot.getValue(String.class)).thenReturn(id);

        DatabaseReference childRefMember = mock(DatabaseReference.class);
        DatabaseReference childRefMemberRestaurantsLikedBy = mock(DatabaseReference.class);
        when(ref.child(testMember.getName())).thenReturn(childRefMember);
        when(childRefMember.child("restaurantsLikedBy")).thenReturn(childRefMemberRestaurantsLikedBy);

        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(snapshot);
            return null;
        }).when(childRefMemberRestaurantsLikedBy).addValueEventListener(any(ValueEventListener.class));
        repository.getActiveMemberLikedRestaurantLiveDataList(testMember).observeForever(observerStringList);
        verify(observerStringList).onChanged(Arrays.asList(id));
    }

    @Test
    public void testDeleteLikedRestaurant() {
        Member testMember = new Member("123", "test A", "abc@test.com", "", "", "", false);
        String restaurantId = "restaurant123";

        DatabaseReference childRefMember = mock();
        DatabaseReference childRefMemberRestaurantsLikedBy = mock();
        DatabaseReference childRefMemberRestaurantId = mock();
        when(ref.child(testMember.getName())).thenReturn(childRefMember);
        when(childRefMember.child("restaurantsLikedBy")).thenReturn(childRefMemberRestaurantsLikedBy);
        when(childRefMemberRestaurantsLikedBy.child(restaurantId)).thenReturn(childRefMemberRestaurantId);

        repository.deleteLikedRestaurant(testMember, restaurantId);
        verify(ref.child(testMember.getName()).child("restaurantsLikedBy").child(restaurantId)).removeValue();
    }

    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************
    @Test
    public void testAddSelectedRestaurant() {
        {
            SelectedRestaurant testSelectedRestaurant = new SelectedRestaurant("restaurant123", "restaurant123", 1);

            DatabaseReference childRefId = mock();
            when(selectedRestaurantsRef.child(testSelectedRestaurant.getRestaurantId())).thenReturn(childRefId);

            repository.addSelectedRestaurant(testSelectedRestaurant);
            verify(selectedRestaurantsRef.child(testSelectedRestaurant.getRestaurantId())).setValue(testSelectedRestaurant);
        }
    }

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


    @Test
    public void testDeleteSelectedRestaurant() {
        String restaurantId = "restaurant123";

        DatabaseReference childRefId = mock();
        when(selectedRestaurantsRef.child(restaurantId)).thenReturn(childRefId);

        repository.deleteSelectedRestaurant(restaurantId);
        verify(selectedRestaurantsRef.child(restaurantId)).removeValue();
    }

    @Test
    public void testUpdateSelectedRestaurant() {
        SelectedRestaurant testSelectedRestaurant = new SelectedRestaurant("restaurant123", "restaurant123", 5);

        DatabaseReference childRefId = mock();
        DatabaseReference childRefMemberJoiningNumber = mock();
        when(selectedRestaurantsRef.child(testSelectedRestaurant.getRestaurantId())).thenReturn(childRefId);
        when(childRefId.child("memberJoiningNumber")).thenReturn(childRefMemberJoiningNumber);

        repository.updateSelectedRestaurant(testSelectedRestaurant);
        verify(selectedRestaurantsRef.child(testSelectedRestaurant.getRestaurantId()).child("memberJoiningNumber"))
                .setValue(testSelectedRestaurant.getMemberJoiningNumber());
    }


    // ****************************************
    // ***** SELECTED RESTAURANTS MEMBERS *****
    // ****************************************

    @Test
    public void testAddMemberToSelectedRestaurantMemberList() {
        Member testMember = new Member();
        testMember.setMemberId("member123");
        String selectedRestaurantId = "restaurant123";

        DatabaseReference childRefId = mock();
        DatabaseReference childRefSelectedBy = mock();
        DatabaseReference childRefMemberId = mock();
        when(selectedRestaurantsRef.child(selectedRestaurantId)).thenReturn(childRefId);
        when(childRefId.child("restaurantSelectedBy")).thenReturn(childRefSelectedBy);
        when(childRefSelectedBy.child(testMember.getMemberId())).thenReturn(childRefMemberId);

        repository.addMemberToSelectedRestaurantMemberList(testMember, selectedRestaurantId);
        verify(selectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy")
                .child(testMember.getMemberId())).setValue(testMember);
    }

    @Test
    public void testDeleteMemberToSelectedRestaurantMemberList() {
        Member testMember = new Member();
        testMember.setMemberId("member123");
        String selectedRestaurantId = "restaurant123";

        DatabaseReference childRefId = mock();
        DatabaseReference childRefSelectedBy = mock();
        DatabaseReference childRefMemberId = mock();
        when(selectedRestaurantsRef.child(selectedRestaurantId)).thenReturn(childRefId);
        when(childRefId.child("restaurantSelectedBy")).thenReturn(childRefSelectedBy);
        when(childRefSelectedBy.child(testMember.getMemberId())).thenReturn(childRefMemberId);

        repository.deleteMemberToSelectedRestaurantMemberList(testMember, selectedRestaurantId);
        verify(selectedRestaurantsRef.child(selectedRestaurantId).child("restaurantSelectedBy")
                .child(testMember.getMemberId())).removeValue();
    }

}