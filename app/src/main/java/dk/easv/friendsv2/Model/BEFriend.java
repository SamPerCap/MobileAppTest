package dk.easv.friendsv2.Model;



import android.media.Image;
import android.widget.ImageView;

import java.io.Serializable;

public class BEFriend implements Serializable {

    private String m_name;
    private String m_phone;
    private String m_email;
    private int m_profilePicture;
    private Boolean m_isFavorite;

    public BEFriend(String name, String phone, String email, int profilePicture) {
        this(name, phone, email, profilePicture, false);
    }

    public BEFriend(String name, String phone, String email, int profilePicture, Boolean isFavorite) {
        m_name = name;
        m_phone = phone;
        m_email = email;
        m_profilePicture = profilePicture;
        m_isFavorite = isFavorite;
    }

    public String getPhone() {
        return m_phone;
    }

    public String getEmail() {
        return m_email;
    }

    public int getImage() {
        return m_profilePicture;
    }


    public String getName() {
        return m_name;
    }

    public Boolean isFavorite() { return m_isFavorite; }


}
