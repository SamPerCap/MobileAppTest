package dk.easv.friendsv2.Model;



import java.io.Serializable;

public class BEFriend implements Serializable {

    private String m_name;
    private String m_phone;
    private String m_email;
    private String m_url;
    private Boolean m_isFavorite;

    public BEFriend(String name, String phone, String email, String url) {
        this(name, phone, email, url, false);
    }

    public BEFriend(String name, String phone, String email, String url, Boolean isFavorite) {
        m_name = name;
        m_phone = phone;
        m_email = email;
        m_url = url;
        m_isFavorite = isFavorite;
    }

    public String getPhone() {
        return m_phone;
    }

    public String getEmail() {
        return m_email;
    }

    public String getUrl() {
        return m_url;
    }


    public String getName() {
        return m_name;
    }

    public Boolean isFavorite() { return m_isFavorite; }


}
