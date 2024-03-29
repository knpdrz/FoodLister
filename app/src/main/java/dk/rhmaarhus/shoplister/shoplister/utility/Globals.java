package dk.rhmaarhus.shoplister.shoplister.utility;

/**
 * Created by Moon on 30.11.2017.
 */

public class Globals {
    public static final String TAG = "shopListerLog";

    //for extras in intents
    public static final String LIST_ID = "listID";
    public static final String LIST_NAME="listName";

    //request codes
    public static final int LIST_DETAILS_REQ_CODE = 1;
    public static final int SHARE_SCREEN_REQ_CODE = 2;
    public static final int SETTINGS_REQ_CODE = 3;
    public static final int RC_SIGN_IN = 123;

    //result codes
    public static final int RESULT_UNFOLLOW = 20;

    //database references
    public static final String LIST_NODE = "lists";
    public static final String USERS_LISTS_NODE = "usersLists";
    public static final String USER_INFO_NODE = "userInfo";
    public static final String LIST_MEMBERS_NODE = "listMembers";
    public static final String SHOPPING_ITEMS_NODE = "shoppingItems";
    public static final String CHAT_NODE = "chat";
    public static final String EMAIL_NODE = "email";

    //network
    public static final String CONNECT = "CONNECTIVITY";

    //notifications
    public static final String NOTIFICATION_CHANNEL_ID = "HRM_FOODLISTER_ID";
    public static final String NOTIFICATION_CHANNEL_NAME = "HRM_FOODLISTER_APP";
    public static final int UNIQUE_NOTIFICATION_ID = 1991;



}
