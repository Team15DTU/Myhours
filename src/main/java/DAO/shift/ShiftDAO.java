package DAO.shift;

import DTOs.shift.IShiftDTO;
import DTOs.shift.ShiftDTO;
import DTOs.worker.WorkerDTO;
import db.IConnPool;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Rasmus Sander Larsen
 */
public class ShiftDAO implements IShiftDAO {

    /*
    -------------------------- Fields --------------------------
     */

    private IConnPool iConnPool;
    
    
    /*
    ----------------------- Constructor -------------------------
     */
    
    public ShiftDAO (IConnPool iConnPool) {
        this.iConnPool = iConnPool;
    }
    
    /*
    ------------------------ Properties -------------------------
     */

    // <editor-folder desc="Properties"


    // </editor-folder>
    
    /*
    ---------------------- Public Methods -----------------------
     */

    @Override
    public IShiftDTO getIShift(int userID, LocalDateTime dateAndTime) {
        return null;
    }

    @Override
    public List<IShiftDTO> getIShiftList() {
        return null;
    }

    @Override
    public List<IShiftDTO> getIShiftList(int jobID) {
        return null;
    }

    @Override
    public List<IShiftDTO> getIShiftList(int jobID, LocalDateTime fromDateTime, LocalDateTime toDateTime) {
        return null;
    }

    @Override
    public void createIShift(IShiftDTO shift) {

    }

    @Override
    public void updateIShift(IShiftDTO shift) {

    }

    @Override
    public void deleteIShift(int userID, LocalDateTime dateAndTime) {

    }

    /*
    ---------------------- Support Methods ----------------------
     */


}
