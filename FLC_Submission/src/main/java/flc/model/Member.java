package flc.model;

public class Member {
    private final String memberId;
    private final String name;
    private final String email;

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "[" + memberId + "] " + name;
    }
}
