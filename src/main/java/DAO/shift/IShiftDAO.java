package DAO.shift;

import DTOs.shift.ShiftDTO;
import DTOs.worker.WorkerDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Rasmus Sander Larsen
 */
public interface IShiftDAO {

    /*
    ---------------------- Public Methods -----------------------
     */

    // returns a ShiftDTO from an UserID and a Date and time of the shift TODO: Maybe use ShiftID?.
    ShiftDTO getShift (int userID, LocalDateTime dateAndTime);

    // Returns a List of ShiftDTO object.
    List<ShiftDTO> getShiftList ();

    // Returns a List of ShiftDTO object.
    List<ShiftDTO> getShiftList (int jobID);

    // Returns a List of ShiftDTO object.
    List<ShiftDTO> getShiftList (int jobID, LocalDateTime fromDateTime, LocalDateTime toDateTime);

    //TODO: LIST som får alle en brugers vagter.

    // Inserts the data from a ShiftDTO into DB.
    void createShift (ShiftDTO shift);

    // Updates the data on a Shifts row in the DB.
    void updateShift (ShiftDTO shift);

    // Deletes all information about one Shift, from an UserID and a Date and time of the shift TODO: Maybe use ShiftID?.
    void deleteShift (int userID, LocalDateTime dateAndTime);

}
