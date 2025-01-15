import java.util.ArrayList;
import java.util.List;

public class Connection {

    public static int power = 3;

    public static List<Connection> allConnections = new ArrayList<>(); //Sorted by innovation Number

    private Neuron n1;
    private Neuron n2;

    public Connection(Neuron n1, Neuron n2)
    {
        this.n1 = n1;
        this.n2 = n2;

        insertConnection(this);
    }

    public Neuron getNueron1()
    {
        return this.n1;
    }

    public Neuron getNueron2()
    {
        return this.n2;
    }

    public int getInnovation1()
    {
        return this.n1.getInnovationNumber();
    }

    public int getInnovation2()
    {
        return this.n2.getInnovationNumber();
    }

    public static Connection getConnection(Neuron n1, Neuron n2)
    {
        if(allConnections.size() == 0)
        {
            return null;
        }

        int low = 0;
        int high = allConnections.size()-1;

        while(low <= high)
        {
            int mid = (low+high)/2;
            if(allConnections.get(mid).getInnovation1() > n1.getInnovationNumber())
            {
                high = mid-1;
            }
            else if(allConnections.get(mid).getInnovation1() < n1.getInnovationNumber())
            {
                low = mid+1;
            }
            else if(allConnections.get(mid).getInnovation2() > n2.getInnovationNumber())
            {
                high = mid-1;
            }
            else if(allConnections.get(mid).getInnovation2() < n2.getInnovationNumber())
            {
                low = mid+1;
            }
            else
            {
                return allConnections.get(mid);
            }
        }

        return null;
    }

    public static int getInsertionIndex(Connection c)
    {
        int low = 0;
        int high = allConnections.size();
        int mid = -1;

        while(low < high)
        {
            mid = (low+high)/2;

            if(allConnections.get(mid).getInnovation1() > c.getNueron1().getInnovationNumber())
            {
                high = mid;
            }
            else if(allConnections.get(mid).getInnovation1() < c.getNueron1().getInnovationNumber())
            {
                low = mid+1;
            }
            else if(allConnections.get(mid).getInnovation2() > c.getNueron2().getInnovationNumber())
            {
                high = mid;
            }
            else if(allConnections.get(mid).getInnovation2() < c.getNueron2().getInnovationNumber())
            {
                low = mid+1;
            }
            else
            {
                return -1;
            }
        }

        if(allConnections.size() == 0)
        {
            return 0;
        }

        return mid;
    }

    public static void insertConnection(Connection c)
    {
        int index = getInsertionIndex(c);
        if(index!=-1)
        {
            allConnections.add(index, c);
        }
        else
        {
            allConnections.add(c);
        }
    }

    //Sorts the cords by x value ascending then by z value ascending
    public static void mergeSortConnections(List<Connection> connections, int low, int high) {
        if (high <= low) return;

        int mid = (low+high)/2;
        mergeSortConnections(connections, low, mid);
        mergeSortConnections(connections, mid+1, high);
        mergeConnections(connections, low, mid, high);
    }

    public static void mergeConnections(List<Connection> connections, int low, int mid, int high) {
        // Creating temporary subarrays
        Connection leftArray[] = new Connection[mid - low + 1];
        Connection rightArray[] = new Connection[high - mid];

        // Copying our subarrays into temporaries
        for (int i = 0; i < leftArray.length; i++)
            leftArray[i] = connections.get(low + i);
        for (int i = 0; i < rightArray.length; i++)
            rightArray[i] = connections.get(mid + i + 1);

        // Iterators containing current index of temp subarrays
        int leftIndex = 0;
        int rightIndex = 0;

        // Copying from leftArray and rightArray back into array
        for (int i = low; i < high + 1; i++) {
            // If there are still uncopied elements in R and L, copy minimum of the two
            if (leftIndex < leftArray.length && rightIndex < rightArray.length) {
                if (leftArray[leftIndex].getInnovation1() < rightArray[rightIndex].getInnovation1()) {
                    connections.set(i, leftArray[leftIndex]);
                    leftIndex++;
                }
                else if (leftArray[leftIndex].getInnovation1() > rightArray[rightIndex].getInnovation1()){
                    connections.set(i, rightArray[rightIndex]);
                    rightIndex++;
                }
                else
                {
                    if (leftArray[leftIndex].getInnovation2() < rightArray[rightIndex].getInnovation2()) {
                        connections.set(i, leftArray[leftIndex]);
                        leftIndex++;
                    }
                    else if (leftArray[leftIndex].getInnovation2() > rightArray[rightIndex].getInnovation2()){
                        connections.set(i, rightArray[rightIndex]);
                        rightIndex++;
                    }
                    else
                    {
                        System.out.println("ERROR, attempting to sort 2 equal connections");
                    }
                }
            } else if (leftIndex < leftArray.length) {
                // If all elements have been copied from rightArray, copy rest of leftArray
                connections.set(i, leftArray[leftIndex]);
                leftIndex++;
            } else if (rightIndex < rightArray.length) {
                // If all elements have been copied from leftArray, copy rest of rightArray
                connections.set(i, rightArray[rightIndex]);
                rightIndex++;
            }
        }
    }

}
