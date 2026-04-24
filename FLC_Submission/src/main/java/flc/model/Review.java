package flc.model;

public class Review {
    private final String memberId;
    private final String comment;
    private final int rating;

    public Review(String memberId, String comment, int rating) {
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1-5");
        this.memberId = memberId;
        this.comment = comment;
        this.rating = rating;
    }

    public String getMemberId() { return memberId; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }

    @Override
    public String toString() {
        return "Rating: " + rating + "/5 - \"" + comment + "\" (Member: " + memberId + ")";
    }
}
