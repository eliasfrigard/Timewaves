package sql;

import java.io.*;
import java.util.ArrayList;

public class SQLTester {
    public static void main(String[] args) throws IOException {

        /*
        ArrayList<TimelineEvent> eventList = new ArrayList<>();
        ArrayList<InputStream> streamList = new ArrayList<>();
        byte[][] arrayOfArrays = new byte[9][20];

        eventList.add(TimeNodeMethods.selectEvent(207));
        eventList.add(TimeNodeMethods.selectEvent(204));
        eventList.add(TimeNodeMethods.selectEvent(209));
        eventList.add(TimeNodeMethods.selectEvent(202));
        eventList.add(TimeNodeMethods.selectEvent(203));
        eventList.add(TimeNodeMethods.selectEvent(201));
        eventList.add(TimeNodeMethods.selectEvent(198));
        eventList.add(TimeNodeMethods.selectEvent(205));
        eventList.add(TimeNodeMethods.selectEvent(208));

        Iterator<TimelineEvent> eventIt = eventList.iterator();


        while (eventIt.hasNext()) {
            streamList.add(eventIt.next().getImageStream());
        }

        Iterator<InputStream> streamIt = streamList.iterator();
        int index = 0;

        while(streamIt.hasNext()) {
            byte[] currentArray = arrayOfArrays[index];
            streamIt.next().read(currentArray);
            index++;
        }

        String printString = "";

        for (int i = 0; i < arrayOfArrays.length; i++) {
            byte[] currentArray = arrayOfArrays[i];

            for (int j = 0; j < currentArray.length; j++) {
                printString += currentArray[j] + " ";
            }
            printString += "\n";
        }

        System.out.println(printString);

         */

        ArrayList<String> kws = TimelineMethods.getExistingKeywords();

        for (String word : kws) {
            System.out.println(word);
        }
    }
}
