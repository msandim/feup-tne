package recommender.data;

import java.util.List;

/**
 * Created by Miguel on 25-05-2016.
 */
public class User
{
    private Long id;

    private List<Long> itemIDs;
    private List<Long> userIDs;

    public User(Long id)
    {
        this.id = id;
    }

    public void addItemID(Long itemID)
    {
        itemIDs.add(itemID);
    }

    public void addUserID(Long userID)
    {
        userIDs.add(userID);
    }
}
