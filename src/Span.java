public class Span {
    long lineNum;
    int posBegin, posEnd;

    public Span(long lineNum, int posBegin, int posEnd) {
        this.lineNum = lineNum;
        this.posBegin = posBegin;
        this.posEnd = posEnd;
    }

    @Override
    public String toString() {
        return String.format("Line: %d, Range: %d - %d", lineNum, posBegin, posEnd);
    }
}
