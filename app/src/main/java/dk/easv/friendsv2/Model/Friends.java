package dk.easv.friendsv2.Model;

import java.util.ArrayList;

import dk.easv.friendsv2.R;

public class Friends {

	ArrayList<BEFriend> m_friends;
	
	public Friends()
	{
		m_friends = new ArrayList<BEFriend>();
		m_friends.add(new BEFriend("Kristofer", "52525311", "kris86e1@easv365.dk", R.drawable.kris_photo, true));
		m_friends.add(new BEFriend("Jacob", "000000","jaco4998@easv365.dk", R.drawable.jacob_photo));
		m_friends.add(new BEFriend("Samuel", "000000","samu1667@easv365.dk", R.drawable.kris_photo, true));

	}
	
	public ArrayList<BEFriend> getAll()
	{ return m_friends; }
	
	public String[] getNames()
	{
		String[] res = new String[m_friends.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = m_friends.get(i).getName();
		}
		return res;
	}

}
