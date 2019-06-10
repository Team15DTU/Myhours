package db;

import DAO.DALException;
import DAO.job.IJobDAO;
import DAO.job.JobDAO;
import DAO.activity.IActivityDAO;
import DAO.activity.ActivityDAO;
import DAO.employer.IEmployerDAO;
import DAO.employer.EmployerDAO;
import DAO.worker.IWorkerDAO;
import DAO.worker.WorkerDAO;
import DTOs.job.IJobDTO;
import DTOs.activity.IActivityDTO;
import DTOs.workPlace.IEmployerDTO;
import DTOs.worker.IWorkerDTO;

import java.sql.*;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Rasmus Sander Larsen
 */
public class DBController implements IDBController {

    /*
    -------------------------- Fields --------------------------
     */
    
    private IConnPool connPool;
    private IWorkerDAO iWorkerDAO;
    private IEmployerDAO iEmployerDAO;
    private IJobDAO iJobDAO;
    private IActivityDAO iActivityDAO;
    
    /*
    ----------------------- Constructor -------------------------
     */
    
    public DBController (IConnPool connPool) throws DALException
    {

        this.connPool = connPool;

        TimeZone.setDefault(TimeZone.getTimeZone(setTimeZoneFromSQLServer()));

        iWorkerDAO      = new WorkerDAO(this.connPool);
        iEmployerDAO = new EmployerDAO(this.connPool);
        iJobDAO         = new JobDAO(this.connPool);
        iActivityDAO = new ActivityDAO(this.connPool);

    }
    
    /*
    ------------------------ Properties -------------------------
     */

    // <editor-folder desc="Properties"

    public IWorkerDAO getiWorkerDAO() {
        return iWorkerDAO;
    }

    public void setiWorkerDAO(IWorkerDAO iWorkerDAO) {
        this.iWorkerDAO = iWorkerDAO;
    }

    public IEmployerDAO getiEmployerDAO() {
        return iEmployerDAO;
    }

    public void setiEmployerDAO(IEmployerDAO iEmployerDAO) {
        this.iEmployerDAO = iEmployerDAO;
    }

    public IJobDAO getiJobDAO() {
        return iJobDAO;
    }

    public void setiJobDAO(IJobDAO iJobDAO) {
        this.iJobDAO = iJobDAO;
    }

    public IActivityDAO getiActivityDAO() {
        return iActivityDAO;
    }

    public void setiActivityDAO(IActivityDAO iActivityDAO) {
        this.iActivityDAO = iActivityDAO;
    }


    // </editor-folder>
    
    /*
    ---------------------- Public Methods -----------------------
     */

    public int getNextAutoIncremental(String tableName) throws DALException
    {
        Connection c = connPool.getConn();

        try {

            Statement statement = c.createStatement();
            statement.executeQuery("ANALYZE TABLE " + tableName);

            PreparedStatement pStatement = c.prepareStatement(
                    "SELECT `auto_increment` FROM INFORMATION_SCHEMA.TABLES " +
                            " WHERE table_name = ?");
            pStatement.setString(1, tableName);

            ResultSet resultset = pStatement.executeQuery();

            resultset.next();

            return resultset.getInt(1);

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        } finally {
            connPool.releaseConnection(c);
        }
    }

    /**
     * This methods returns a FULL IWorkerDTO Object.
     * Including:
     * 1) A list of the workers Workplaces.
     * 2) Each of those Workplaces contains a list of its Jobs
     * 3) Each of those Jobs contains a list of its Shifts
     * @param email We find the Worker, from its email as it is unique
     * @return A IWorkerDTO
     * @throws DALException Will throw a DALException.
     */
    public IWorkerDTO getIWorkerDTO (String email) throws DALException
    {
        // Get the worker from DB, and make object
        IWorkerDTO worker = createFullIWorkerDTO(email);
        
        if ( worker == null )
            System.err.println("ERROR: Couldn't create WorkerDTO object");
        
        return worker;
    }


    /*
    ---------------------- Support Methods ----------------------
     */

    public String setTimeZoneFromSQLServer ()  throws DALException
    {
        Connection c = connPool.getConn();
        try {
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT @@system_time_zone");
            resultSet.next();
            return resultSet.getString(1);

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        } finally {
            connPool.releaseConnection(c);
        }
    }

    public void createWorker (IWorkerDTO workerDTO, String password) throws DALException
    {
        iWorkerDAO.createWorker(workerDTO,password);
    }

    /**
     * This methods returns a FULL IWorkerDTO Object.
     * Including:
     * 1) A list of the workers Workplaces.
     * 2) Each of those Workplaces contains a list of its Jobs
     * 3) Each of those Jobs contains a list of its Shifts
     * @param email We find the Worker, from its email as it is unique
     * @return A IWorkerDTO
     * @throws DALException Will throw a DALException.
     */
    private IWorkerDTO createFullIWorkerDTO (String email) throws DALException
    {
        // Gets the IWorkerDTO
        IWorkerDTO workerDTOToReturn = iWorkerDAO.getWorker(email);
        
        // Sets WorkerDTOs List<IWorkplaceDTO> workplaces via WorkplaceDAO.
        workerDTOToReturn.setIWorkPlaces(iEmployerDAO.getIWorkPlaceList(workerDTOToReturn.getWorkerID()));
        
        // Sets WorkplaceDTOs List<IJobDTO> jobList via JobDAO, for each WorkplaceDTO in Workers List<WorkplaceDTO>
        for (IEmployerDTO workPlaceDTO : workerDTOToReturn.getIWorkPlaces()) {
            List<IJobDTO> iJobDToList = iJobDAO.getIJobList(workPlaceDTO.getWorkplaceID());
            workPlaceDTO.setIJobList(iJobDToList);
        }
        // Sets JobDTOs List<IActivityDTO> shiftList via ActivityDAO, for each IJobDTO in each IWorkplaceDTO in Workers List<WorkplaceDTO>
        for (IEmployerDTO iworkPlaceDTO : workerDTOToReturn.getIWorkPlaces()) {
            for (IJobDTO iJobDTO : iworkPlaceDTO.getIJobList()) {
                List<IActivityDTO> iActivityDTOList = iActivityDAO.getIShiftList(iJobDTO.getJobID());
                iJobDTO.setIShiftDTOList(iActivityDTOList);
            }
        }

        return workerDTOToReturn;
    }

}
