package DAO.worker;

import DAO.DALException;
import DTOs.worker.IWorkerDTO;
import DTOs.worker.WorkerDTO;
import db.IConnPool;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rasmus Sander Larsen
 */
@Path("/Test3")
public class WorkerDAO implements IWorkerDAO {

    /*
    -------------------------- Fields --------------------------
     */
    
    private IConnPool connPool;
    private final String WORKERS_TABLENAME = "Workers";
    
    /*
    ----------------------- Constructor -------------------------
     */
    
    public WorkerDAO(IConnPool connPool)
    {
        this.connPool = connPool;
    }

    public WorkerDAO(){

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
    public IWorkerDTO getWorker(String email) throws DALException
    {
        IWorkerDTO workerToReturn = new WorkerDTO();
        
        // Get connection from ConnPool
        Connection c = connPool.getConn();

        try {
            Statement stmtWorkers = c.createStatement();    // Stmt to get Worker Resultset
            ResultSet resultSet = stmtWorkers.executeQuery( //TODO: Need to be made preparedstmt
                    "SELECT * FROM " + WORKERS_TABLENAME + " WHERE " + Columns.email.toString() + " = '" + email + "'");

            while (resultSet.next()) {
                workerToReturn.setWorkerID(resultSet.getInt("workerid"));
                workerToReturn.setFirstName(resultSet.getString("firstname"));
                workerToReturn.setSurName(resultSet.getString("surname"));
                workerToReturn.setEmail(resultSet.getString("email"));
                workerToReturn.setBirthday(resultSet.getDate("birthday").toLocalDate());
            }

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        } finally {
            // Return the Connection
            connPool.releaseConnection(c);
        }

        return workerToReturn;
    }

    @Override
    public List<IWorkerDTO> getWorkerList() throws DALException
    {
        // Get connection from the ConnPool
        Connection c = connPool.getConn();

        List<IWorkerDTO> listToReturn = new ArrayList<>();

        try {

            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + Columns.email.toString() + " FROM " + WORKERS_TABLENAME);

            while (resultSet.next()) {
                listToReturn.add(getWorker(resultSet.getString(Columns.email.toString())));
            }

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        } finally {
            // Return the connection to the ConnPool
            connPool.releaseConnection(c);
        }

        return listToReturn;
    }

    @Override
    public void createWorker(IWorkerDTO workerDTO) throws DALException
    {
        // Get a Connection from the ConnPool
        Connection c = connPool.getConn();
        
        // The query to make
        String query =
                String.format("INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?,?,?,?,?)",
                        WORKERS_TABLENAME, Columns.firstname, Columns.surname, Columns.email,
                        Columns.birthday, Columns.pass);
                
        try {

            PreparedStatement statement = c.prepareStatement(query);
            
            statement.setString(1, workerDTO.getFirstName());
            statement.setString(2, workerDTO.getSurName());
            statement.setString(3, workerDTO.getEmail());
            statement.setDate(4, Date.valueOf(workerDTO.getBirthday()));
            statement.setString(5, workerDTO.getPassword());

            statement.executeUpdate();

            // TODO: Print skal fjernes på et tidspunkt.
            System.out.println("Worker have been added to: \t DB: myhours \tTable: " + WORKERS_TABLENAME);

        }
        catch (SQLException e) {
            throw new DALException(e.getMessage());
        }
        finally {
            // Return the Connection to the Pool
            connPool.releaseConnection(c);
        }
    }

    @Override
    public int updateWorker(IWorkerDTO worker, String password) throws DALException
    {
        // Get connection from ConnPool
        Connection c = connPool.getConn();

        int rowsAltered;
        
        // The query to make
        String query = String.format("UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = %d",
                WORKERS_TABLENAME, Columns.firstname, Columns.surname, Columns.email, Columns.birthday,
                Columns.pass, Columns.workerId, worker.getWorkerID());

        try {

            PreparedStatement pStatement = c.prepareStatement(query);

            pStatement.setString(1, worker.getFirstName());
            pStatement.setString(2, worker.getSurName());
            pStatement.setString(3, worker.getEmail());
            pStatement.setDate(4, Date.valueOf(worker.getBirthday()));
            pStatement.setString(5, password);

            rowsAltered = pStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DALException(e.getMessage());
        } finally {
            // Return the connection to ConnPool
            connPool.releaseConnection(c);
        }
        
        // Return the number of Rows effected by the update
        return rowsAltered;
    }

    @Override
    public void deleteWorker(String email) throws DALException
    {
        // Get connection from ConnPool
        Connection c = connPool.getConn();
        try {

            PreparedStatement pStatement =
                    c.prepareStatement("DELETE FROM " + WORKERS_TABLENAME + " WHERE " + Columns.email + " = ?");
            
            pStatement.setString(1, email);

            pStatement.executeUpdate();

        } catch (SQLException e ) {
            throw new DALException(e.getMessage());
        } finally {
            // Return connection to ConnPool
            connPool.releaseConnection(c);
        }
    }
    @POST
    @Path("/hey")
    @Consumes(MediaType.APPLICATION_JSON)
    public boolean loginCheck(WorkerDTO user) {
        System.out.println(user);
        System.out.println(user.getEmail() + " " + user.getPassword());

        System.out.println(user);

        //System.out.println(worker.getEmail()+" "+worker.getPassword());

        return true;
    }

    /*
    ---------------------- Support Methods ----------------------
     */
    
    private enum Columns {
    	workerId,
		firstname,
		surname,
		email,
		birthday,
		pass
	}
}
