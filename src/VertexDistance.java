/**
 * @author dimz
 * @since 1/4/18.
 */
class VertexDistance<T> {
    private T myVertex;
    private int distance;

    VertexDistance(T myVertex, int distance) {
        this.myVertex = myVertex;
        this.distance = distance;
    }

    T getMyVertex() {
        return myVertex;
    }

    int getDistance() {
        return distance;
    }
}