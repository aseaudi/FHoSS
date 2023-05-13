package de.fhg.fokus.hss.web.action;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.apache.log4j.Logger;

import de.fhg.fokus.hss.db.model.IMPI;
import de.fhg.fokus.hss.db.model.IMSU;
import de.fhg.fokus.hss.db.op.IMPI_DAO;
import de.fhg.fokus.hss.db.op.IMSU_DAO;
import de.fhg.fokus.hss.db.hibernate.*;

class AddSubsFHoSS {
    private static Logger logger = Logger.getLogger(AddSubsFHoSS.class);
    public static void main(String[] args) {
        System.out.println("Adding Subscribers to FHoSS ..."); 
        boolean dbException = false;
        try{
            Session session = HibernateUtil.getCurrentSession();
            HibernateUtil.beginTransaction();
            IMSU imsu = null;
            imsu = new IMSU();
            imsu.setName(args[0]);
            imsu.setId_capabilities_set(Integer.parseInt(args[1]));
            imsu.setId_preferred_scscf_set(Integer.parseInt(args[2]));
            IMSU_DAO.insert(session, imsu);
            Integer id = imsu.getId();
            System.out.println("Added Subscriber to FHoSS with id: " + id); 
        }                
        catch (HibernateException e){
            logger.error("Hibernate Exception occured!\nReason:" + e.getMessage());
            e.printStackTrace();
            dbException = true;
        }
        finally{
            if (!dbException){
                HibernateUtil.commitTransaction();
            }
            HibernateUtil.closeSession();
        }
    }
}

