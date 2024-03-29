/* 
 * AFTER RUNNING PROJECT WITH COMMAND: 
 * `gradle build && java -Dserver.port=0080 -jar build/libs/gs-spring-boot-0.1.0.jar`
 * RUN CURL COMMAND:
 * `curl {baseUrl}/recordings`
 * EXPECT JSON TO BE RETURNED:
 * [{"uri":"/Accounts/{accountId}/Recordings/",
 * "dateCreated":"{dateCreated}",
 * "dateUpdated":"{dateUpdated}",
 * "revision":1,
 * "recordingId":"{recordingId}",
 * "accountId":"{accountId}",
 * "callId":"{callId}",
 * "durationSec":2,
 * "conferenceId":{conferenceId}}, ...]
*/

package main.java.list_recording;

import com.vailsys.persephony.api.PersyClient;
import com.vailsys.persephony.api.PersyException;
import com.vailsys.persephony.api.recording.Recording;
import com.vailsys.persephony.api.recording.RecordingList;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@RestController
public class ListRecordingController {
  // Get accountID and authToken from environment variables
  private String accountId = System.getenv("ACCOUNT_ID");
  private String authToken = System.getenv("AUTH_TOKEN");

  @RequestMapping("/recordings")
  public ArrayList<Recording> listRecordings() {
    PersyClient client;
    RecordingList recordingsList;

    try {
      client = new PersyClient(accountId, authToken); // Create PersyClient object
      recordingsList = client.recordings.getMeta(); // Retrieve the paginated list of recordings

      // Don't bother trying to grab more pages if there is only one or zero
      // pages of results
      if (recordingsList.getTotalSize() > recordingsList.getPageSize()) {
        // Load in all the recordings returned.
        while (recordingsList.getLocalSize() < recordingsList.getTotalSize()) {
          recordingsList.loadNextPage(); // Load in the next page of recordings.
        }
      }

      ArrayList<Recording> allRecordings = recordingsList.export(); // Extract the array from the RecordingList

      for (Recording r : allRecordings) {
        // do something with each recording
        System.out.print(r);
      }

      return allRecordings;
    } catch (PersyException pe) {
      System.out.println(pe.getMessage());
    }

    return null;
  }
}