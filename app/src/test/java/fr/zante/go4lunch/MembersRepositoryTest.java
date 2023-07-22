package fr.zante.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import fr.zante.go4lunch.data.MembersRepository;
import fr.zante.go4lunch.model.Member;
import fr.zante.go4lunch.model.SelectedRestaurant;

@RunWith(MockitoJUnitRunner.class)
public class MembersRepositoryTest {


    @Mock
    private DatabaseReference mockMyRef;

    @Mock
    private DatabaseReference mockMyRefLikedRestaurant;

    @Mock
    private DatabaseReference mockMySelectedRestaurantsRef;

    @Mock
    private DatabaseReference mockMySelectedRestaurantMemberListRef;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    // *******************************
    // *********** MEMBERS ***********
    // *******************************
    @Test
    public void testAddMember() {
        // TODO
    }

    @Test
    public void testGetMembersLiveDataList() {
        // Create a mock DataSnapshot with the test members
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);

        List<Member> testMembersList = new ArrayList<>();
        testMembersList.add(new Member("123","John","abc@gmail.com","", "", "", false));
        testMembersList.add(new Member("987","Jane","lol@gmail.com","", "", "", false));

        when(mockSnapshot.getChildren()).thenReturn(createMockChildren(testMembersList));

        // Create a MutableLiveData instance to capture the emitted value
        MutableLiveData<List<Member>> capturedLiveData = new MutableLiveData<>();

        // Stub the addValueEventListener() method to capture the ValueEventListener and invoke its onDataChange()
        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(mockSnapshot);
            return null;
        }).when(mockMyRef).addValueEventListener(any(ValueEventListener.class));

        MembersRepository membersRepository = MembersRepository.getInstance();
        LiveData<List<Member>> resultLiveData = membersRepository.getMembersLiveDataList();

        // Observe the LiveData and capture the emitted value
        resultLiveData.observeForever(capturedLiveData::setValue);

        // Verify that addValueEventListener() was called on the mock DatabaseReference
        verify(mockMyRef).addValueEventListener(any(ValueEventListener.class));

        // Verify that the captured value matches the test members list
        assertEquals(testMembersList, capturedLiveData.getValue());
    }

    @Test
    public void testGetActiveMember() {
        // TODO
    }

    @Test
    public void testUpdateMember() {
        // TODO
    }

    @Test
    public void testUpdateNotificationsAllowed() {
        // TODO
    }


    // **************************************
    // ****** MEMBER LIKED RESTAURANTS ******
    // **************************************
    @Test
    public void testAddLikedRestaurant() {
        // TODO
    }

    @Test
    public void testGetActiveMemberLikedRestaurantLiveDataList() {
        // TODO
        // Create a mock DataSnapshot with the test members
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);

        List<String> testLikedRestaurantList = new ArrayList<>();
        testLikedRestaurantList.add("123");
        testLikedRestaurantList.add("456");

        when(mockSnapshot.getChildren()).thenReturn(createMockChildrenLikedRestaurant(testLikedRestaurantList));

        // Create a MutableLiveData instance to capture the emitted value
        MutableLiveData<List<String>> capturedLiveData = new MutableLiveData<>();

        // Stub the addValueEventListener() method to capture the ValueEventListener and invoke its onDataChange()
        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(mockSnapshot);
            return null;
        }).when(mockMyRefLikedRestaurant).addValueEventListener(any(ValueEventListener.class));

        MembersRepository membersRepository = MembersRepository.getInstance();
        LiveData<List<String>> resultLiveData = membersRepository.getActiveMemberLikedRestaurantLiveDataList(any(Member.class));

        // Observe the LiveData and capture the emitted value
        resultLiveData.observeForever(capturedLiveData::setValue);

        // Verify that addValueEventListener() was called on the mock DatabaseReference
        verify(mockMyRefLikedRestaurant).addValueEventListener(any(ValueEventListener.class));

        // Verify that the captured value matches the test members list
        assertEquals(testLikedRestaurantList, capturedLiveData.getValue());
    }

    @Test
    public void testDeleteLikedRestaurant() {
        // TODO
    }


    // ************************************
    // ******* SELECTED RESTAURANTS *******
    // ************************************
    @Test
    public void testAddSelectedRestaurant() {
        // TODO
    }

    @Test
    public void testGetSelectedRestaurantsLiveDataList() {
        // TODO
        // Create a mock DataSnapshot with the test members
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);

        List<SelectedRestaurant> testSelectedRestaurantList = new ArrayList<>();
        // String restaurantId, String name, int memberJoiningNumber
        testSelectedRestaurantList.add(new SelectedRestaurant("123", "Quick", 1));
        testSelectedRestaurantList.add(new SelectedRestaurant("456", "Mc Do", 2));

        when(mockSnapshot.getChildren()).thenReturn(createMockChildrenSelectedRestaurant(testSelectedRestaurantList));

        // Create a MutableLiveData instance to capture the emitted value
        MutableLiveData<List<SelectedRestaurant>> capturedLiveData = new MutableLiveData<>();

        // Stub the addValueEventListener() method to capture the ValueEventListener and invoke its onDataChange()
        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(mockSnapshot);
            return null;
        }).when(mockMySelectedRestaurantsRef).addValueEventListener(any(ValueEventListener.class));

        MembersRepository membersRepository = MembersRepository.getInstance();
        LiveData<List<SelectedRestaurant>> resultLiveData = membersRepository.getSelectedRestaurantsLiveDataList();

        // Observe the LiveData and capture the emitted value
        resultLiveData.observeForever(capturedLiveData::setValue);

        // Verify that addValueEventListener() was called on the mock DatabaseReference
        verify(mockMySelectedRestaurantsRef).addValueEventListener(any(ValueEventListener.class));

        // Verify that the captured value matches the test members list
        assertEquals(testSelectedRestaurantList, capturedLiveData.getValue());
    }

    @Test
    public void testDeleteSelectedRestaurant() {
        // TODO
    }

    @Test
    public void testUpdateSelectedRestaurant() {
        // TODO
    }


    // ****************************************
    // ***** SELECTED RESTAURANTS MEMBERS *****
    // ****************************************
    @Test
    public void testAddMemberToSelectedRestaurantMemberList() {
        // TODO
    }

    @Test
    public void testGetSelectedRestaurantMemberLiveDataList() {
        // TODO
        // Create a mock DataSnapshot with the test members
        DataSnapshot mockSnapshot = mock(DataSnapshot.class);

        List<Member> testMembersList = new ArrayList<>();
        testMembersList.add(new Member("123","John","abc@gmail.com","", "963", "abc", false));
        testMembersList.add(new Member("987","Jane","lol@gmail.com","", "963", "abc", false));

        when(mockSnapshot.getChildren()).thenReturn(createMockChildren(testMembersList));

        // Create a MutableLiveData instance to capture the emitted value
        MutableLiveData<List<Member>> capturedLiveData = new MutableLiveData<>();

        // Stub the addValueEventListener() method to capture the ValueEventListener and invoke its onDataChange()
        doAnswer(invocation -> {
            ValueEventListener valueEventListener = invocation.getArgument(0);
            valueEventListener.onDataChange(mockSnapshot);
            return null;
        }).when(mockMySelectedRestaurantMemberListRef).addValueEventListener(any(ValueEventListener.class));

        MembersRepository membersRepository = MembersRepository.getInstance();
        LiveData<List<Member>> resultLiveData = membersRepository.getSelectedRestaurantMemberLiveDataList(any(String.class));

        // Observe the LiveData and capture the emitted value
        resultLiveData.observeForever(capturedLiveData::setValue);

        // Verify that addValueEventListener() was called on the mock DatabaseReference
        verify(mockMySelectedRestaurantMemberListRef).addValueEventListener(any(ValueEventListener.class));

        // Verify that the captured value matches the test members list
        assertEquals(testMembersList, capturedLiveData.getValue());
    }

    @Test
    public void testDeleteMemberToSelectedRestaurantMemberList() {
        // TODO
    }


    // Helper method to create a list of mock DataSnapshot children
    private Iterable<DataSnapshot> createMockChildren(List<Member> members) {
        List<DataSnapshot> mockChildren = new ArrayList<>();
        for (Member member : members) {
            DataSnapshot mockChild = mock(DataSnapshot.class);
            when(mockChild.getValue(ArgumentMatchers.<Class<Member>>any())).thenReturn(member);
            //when(mockChild.getValue(Member.class)).thenReturn(member);
            mockChildren.add(mockChild);
        }
        return mockChildren;
    }

    // Helper method to create a list of mock DataSnapshot children
    private Iterable<DataSnapshot> createMockChildrenLikedRestaurant(List<String> likedRestaurantIdList) {
        List<DataSnapshot> mockChildren = new ArrayList<>();
        for (String likedRestaurantId : likedRestaurantIdList) {
            DataSnapshot mockChild = mock(DataSnapshot.class);
            when(mockChild.getValue(String.class)).thenReturn(likedRestaurantId);
            mockChildren.add(mockChild);
        }
        return mockChildren;
    }

    // Helper method to create a list of mock DataSnapshot children
    private Iterable<DataSnapshot> createMockChildrenSelectedRestaurant(List<SelectedRestaurant> selectedRestaurantList) {
        List<DataSnapshot> mockChildren = new ArrayList<>();
        for (SelectedRestaurant selectedRestaurant : selectedRestaurantList) {
            DataSnapshot mockChild = mock(DataSnapshot.class);
            when(mockChild.getValue(SelectedRestaurant.class)).thenReturn(selectedRestaurant);
            mockChildren.add(mockChild);
        }
        return mockChildren;
    }
}
