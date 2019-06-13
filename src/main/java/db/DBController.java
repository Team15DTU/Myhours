package db;

import dao.DALException;
import dao.job.IJobDAO;
import dao.job.JobDAO;
import dao.activity.IActivityDAO;
import dao.activity.ActivityDAO;
import dao.employer.IEmployerDAO;
import dao.employer.EmployerDAO;
import dao.worker.IWorkerDAO;
import dao.worker.WorkerConstants;
import dao.worker.WorkerDAO;
import db.connectionPools.ConnPoolV1;
import dto.job.IJobDTO;
import dto.activity.IActivityDTO;
import dto.employer.IEmployerDTO;
import dto.worker.IWorkerDTO;
import dto.worker.WorkerDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Rasmus Sander Larsen
 */
@Path("/Test")
public class DBController implements IDBController {

    /*
    -------------------------- Fields --------------------------
     */
    
    private static DBController instance;
    
    private IConnPool connPool;
    private IWorkerDAO iWorkerDAO;
    private IEmployerDAO iEmployerDAO;
    private IJobDAO iJobDAO;
    private IActivityDAO iActivityDAO;
    
    /*
    ----------------------- Constructor -------------------------
     */
    
    public DBController ()
    {
        try
        {
            this.connPool   = ConnPoolV1.getInstance();
    
            TimeZone.setDefault(TimeZone.getTimeZone(setTimeZoneFromSQLServer()));
    
            iWorkerDAO      = new WorkerDAO(this.connPool);
            iEmployerDAO    = new EmployerDAO(this.connPool);
            iJobDAO         = new JobDAO(this.connPool);
            iActivityDAO    = new ActivityDAO(this.connPool);
            
            instance = this;
        }
        catch ( DALException e )
        {
            System.err.println("ERROR: DBController constructor Failure - " + e.getMessage());
        }

    }
    
    /*
    ------------------------ Properties -------------------------
     */

    //region Properties
    
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
    
    //endregion
    
    /*
    ---------------------- Public Methods -----------------------
     */
    
	/**
     * Gives the instance of the DBController. If the DBController doesn't
     * exist yet, it will create a new.
     * @return DBController object
     */
    public static DBController getInstance()
    {
        if ( instance == null )
        {
            instance = new DBController();
        }
        
        return instance;
    }
    
    //region Utility
    
    /**
     * This method changes the Connection pool to the given
     * Connection Pool.
     * @param connPool Connection Pool that implements IConnPool interface
     */
    public void changeConnPool(IConnPool connPool)
    {
        //TODO: Implement this
    }
    
    @Override
    public int getNextAutoIncremental(String tableName) throws DALException
    {
        Connection c = connPool.getConn();

        try {

            Statement statement = c.createStatement();
            statement.executeQuery("ANALYZE TABLE " + tableName);

            // Shit works
			//TODO: Fix hardcoded Database
            PreparedStatement pStatement = c.prepareStatement(
                    "SELECT AUTO_INCREMENT FROM information_schema.TABLES where TABLE_SCHEMA = 's185097'" +
							" AND TABLE_NAME = ?" )
					;
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
    
    @Override
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
	
	/**
	 * This method checks if there's a correlation between the
	 * provided email and password. All exceptions is handled by
     * the method.
	 * @return True if there's a correlation
	 */
    @POST
    @Path("/loginCheck")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public boolean loginCheck(WorkerDTO user)
    {
        String email = user.getEmail();
        String password = user.getPassword();

        System.out.println(email);
        System.out.println(password);

    	// Boolean to return
		boolean success = false;
    	
        // Query to be used
        String query  = String.format("SELECT %s, %s FROM %s WHERE %s = ? AND %s = ?",
				WorkerConstants.email, WorkerConstants.password, WorkerConstants.TABLENAME,
				WorkerConstants.email, WorkerConstants.password);
        
		Connection conn = null; PreparedStatement stmt;
        try
        {
            // Get connection from pool
            conn = connPool.getConn();
            
            // Create preparedStatement
            stmt = conn.prepareStatement(query);
            stmt.setString(1, email); stmt.setString(2, password);
            
            // Execute
            ResultSet rs = stmt.executeQuery();
            
            // Check if there was a match
            if ( rs.next() )
                success = true;
	
			// Close statement
			stmt.close();
            
            return success;
        }
        catch ( DALException e )
        {
			System.err.println("ERROR: DALException thrown in loginCheck() - " + e.getMessage());
			return success;
        }
        catch ( SQLException e )
        {
			System.err.println("ERROR: SQLException thrown in loginCheck() - " + e.getMessage());
			return success;
        }
        finally
        {
        	try
			{
				connPool.releaseConnection(conn);
			}
        	catch ( DALException e )
			{
				System.err.println("ERROR: releaseConnection() throwing DAL - " + e.getMessage());
			}
        }
    }
    
    //endregion

    //region Worker
    @POST
    @Path("/createWorker")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public void createWorker (WorkerDTO workerDTO) throws DALException{
        Connection c = connPool.getConn();

        // The query to make
        String query =
                String.format("INSERT INTO %s (%s, %s, %s, %s, %s, %s) VALUES (?,?,?,?,?,?)",
                        WorkerConstants.TABLENAME, WorkerConstants.id, WorkerConstants.firstname, WorkerConstants.surname, WorkerConstants.email,
                        WorkerConstants.birthday, WorkerConstants.password);

        try {

            PreparedStatement statement = c.prepareStatement(query);

            statement.setInt(1, 69);
            statement.setString(2, workerDTO.getFirstName());
            statement.setString(3, workerDTO.getSurName());
            statement.setString(4, workerDTO.getEmail());
            statement.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            statement.setString(6, workerDTO.getPassword());

            statement.executeUpdate();

        }
        catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
        finally {
            // Return the Connection to the Pool
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
    @Override
    public IWorkerDTO getIWorkerDTO (String email) throws DALException
    {
        // Get the worker from DB, and make object
        IWorkerDTO worker = createFullIWorkerDTO(email);
        
        if ( worker == null )
            System.err.println("ERROR: Couldn't create WorkerDTO object");
        
        return worker;
    }
    
    @Override
    public IWorkerDTO getIWorkerDTO (int id) throws DALException
    { return null; }
    
    @Override
    public List<IWorkerDTO> getIWorkerDTOList () throws DALException
    { return null; }
    
    @Override
    public List<IWorkerDTO> getIWorkerDTOList (int minID, int maxID) throws DALException
    { return null; }
    
    @Override
    public List<IWorkerDTO> getIWorkerDTOList (String name) throws DALException
    { return null; }
    
    //endregion
    
    //region Employer
    @Override
    public void createEmployer(IEmployerDTO employer) throws DALException
    { }
    
    @Override
    public IEmployerDTO getIEmployerDTO(int id) throws DALException
    { return null; }
    
    @Override
    public List<IEmployerDTO> getIEmployerList() throws DALException
    { return null; }
    
    @Override
    public List<IEmployerDTO> getIEmployerList(int minID, int maxID) throws DALException
    { return null; }
    
    @Override
    public List<IEmployerDTO> getIEmployerList(String name) throws DALException
    { return null; }
    
    //endregion
    
    //region Job
    
    @Override
    public void createJob(IJobDTO job) throws DALException
    { }
    
    @Override
    public IJobDTO getIJobDTO(int id) throws DALException
    { return null; }
    
    @Override
    public List<IJobDTO> getIJobDTOList() throws DALException
    { return null; }
    
    @Override
    public List<IJobDTO> getIJobDTOList(int employerID) throws DALException
    { return null; }
    
    @Override
    public List<IJobDTO> getIJobDTOList(String name) throws DALException
    { return null; }
    
    @Override
    public List<IJobDTO> getIJobDTOList(double minSalary, double maxSalary) throws DALException
    { return null; }
    
    //endregion
    
    //region Activity
    
    @Override
    public void createActivity(IActivityDTO activity) throws DALException
    { }
    
    @Override
    public IActivityDTO getIActivity(int id) throws DALException
    { return null; }
    
    @Override
    public List<IActivityDTO> getIActivityList() throws DALException
    { return null; }
    
    @Override
    public List<IActivityDTO> getIActivityList(int jobID) throws DALException
    { return null; }
    
    @Override
    public List<IActivityDTO> getIActivityList(Date date) throws DALException
    { return null; }
    
    @Override
    public List<IActivityDTO> getIActivityList(double minVal, double maxVal) throws DALException
    { return null; }
    
    //endregion
    
    /*
    ---------------------- Support Methods ----------------------
     */

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
        
        // Sets WorkerDTOs List<IEmployersDTO> employers via EmployerDAO.
        workerDTOToReturn.setIEmployers(iEmployerDAO.getIWorkPlaceList(workerDTOToReturn.getWorkerID()));
        
        // Sets EmployerDTOs List<IJobDTO> jobList via JobDAO, for each EmployerDTO in Workers List<EmployerDTO>
        for (IEmployerDTO employerDTO : workerDTOToReturn.getIEmployers()) {
            List<IJobDTO> iJobDToList = iJobDAO.getIJobList(employerDTO.getWorkplaceID());
            employerDTO.setIJobList(iJobDToList);
        }
        // Sets JobDTOs List<IActivityDTO> shiftList via ActivityDAO, for each IJobDTO in each IEmployerDTO in Workers List<EmployerDTO>
        for (IEmployerDTO employerDTO : workerDTOToReturn.getIEmployers()) {
            for (IJobDTO iJobDTO : employerDTO.getIJobList()) {
                List<IActivityDTO> iActivityDTOList = iActivityDAO.getIShiftList(iJobDTO.getJobID());
                iJobDTO.setIShiftDTOList(iActivityDTOList);
            }
        }

        return workerDTOToReturn;
    }

}
