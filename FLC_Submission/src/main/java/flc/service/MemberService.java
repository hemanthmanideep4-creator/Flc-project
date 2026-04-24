package flc.service;

import flc.model.Member;

import java.util.*;

public class MemberService {

    private final Map<String, Member> members = new LinkedHashMap<>();

    public MemberService() {
        initMembers();
    }

    private void initMembers() {
        String[][] data = {
            {"M001", "Alice Johnson",   "alice@email.com"},
            {"M002", "Bob Smith",       "bob@email.com"},
            {"M003", "Carol White",     "carol@email.com"},
            {"M004", "David Brown",     "david@email.com"},
            {"M005", "Eve Davis",       "eve@email.com"},
            {"M006", "Frank Miller",    "frank@email.com"},
            {"M007", "Grace Wilson",    "grace@email.com"},
            {"M008", "Hank Moore",      "hank@email.com"},
            {"M009", "Isla Taylor",     "isla@email.com"},
            {"M010", "Jack Anderson",   "jack@email.com"},
        };
        for (String[] d : data) {
            members.put(d[0], new Member(d[0], d[1], d[2]));
        }
    }

    public Optional<Member> getMemberById(String id) {
        return Optional.ofNullable(members.get(id.toUpperCase()));
    }

    public Collection<Member> getAllMembers() {
        return members.values();
    }

    public boolean exists(String memberId) {
        return members.containsKey(memberId.toUpperCase());
    }
}
