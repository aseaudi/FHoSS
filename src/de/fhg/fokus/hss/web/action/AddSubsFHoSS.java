package de.fhg.fokus.hss.web.action;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.apache.log4j.Logger;

import de.fhg.fokus.hss.db.model.IMPI;
import de.fhg.fokus.hss.db.model.IMSU;
import de.fhg.fokus.hss.db.op.IMPI_DAO;
import de.fhg.fokus.hss.db.op.IMSU_DAO;
import de.fhg.fokus.hss.db.hibernate.*;
import de.fhg.fokus.hss.auth.HexCodec;
import de.fhg.fokus.hss.cx.CxConstants;
import de.fhg.fokus.hss.db.model.IMPU;
import de.fhg.fokus.hss.db.model.CxEvents;
import de.fhg.fokus.hss.db.op.IMPI_IMPU_DAO;
import de.fhg.fokus.hss.db.op.IMPU_DAO;
import de.fhg.fokus.hss.db.op.CxEvents_DAO;
import de.fhg.fokus.hss.web.form.IMPI_Form;
import de.fhg.fokus.hss.web.util.WebConstants;
import de.fhg.fokus.hss.zh.ZhConstants;

class AddSubsFHoSS {
    private static Logger logger = Logger.getLogger(AddSubsFHoSS.class);
    public static void main(String[] args) {
        System.out.println("Adding Subscribers to FHoSS ..."); 
        int imsu_id = createIMSU(args);
        int ipmi_id = createIMPI(args);
        int impu_id = createIMPU(args);
        System.out.println("Added Subscriber: " + args[0]); 
    }
    public static int createIMSU(String[] args) {
        int id = -1;
        boolean dbException = false;
        try{
            Session session = HibernateUtil.getCurrentSession();
            HibernateUtil.beginTransaction();
            IMSU imsu = null;
            imsu = new IMSU();
            imsu.setName(args[0]);
            imsu.setDiameter_name("");
            imsu.setScscf_name("");
            imsu.setId_capabilities_set(Integer.parseInt(args[1]));
            imsu.setId_preferred_scscf_set(Integer.parseInt(args[2]));
            IMSU_DAO.insert(session, imsu);
            id = imsu.getId();
            System.out.println("Added IMSU: " + args[0]); 
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
        return id;
    }

    public static int createIMPI(String[] args) {
        int id = -1;
        boolean dbException = false;
        try{
            Session session = HibernateUtil.getCurrentSession();
            HibernateUtil.beginTransaction();
            int auth_scheme = 255;
            IMPI impi;
            String identity = args[0] + "@ims.mnc001.mcc001.3ppnetwork.org";
            String secretKey = args[3];
            String amf = args[4];
            String op = args[5];
            String opc = args[6];
            impi = new IMPI();
            impi.setZh_default_auth_scheme(CxConstants.Auth_Scheme_AKAv1);
            impi.setZh_key_life_time(ZhConstants.Default_Key_Life_Time);
            impi.setZh_uicc_type(ZhConstants.UICC_Type_Basic_GBA);
            impi.setIdentity(identity);
            if (secretKey.length()==32) 
                impi.setK(HexCodec.decode(secretKey));
            else
                impi.setK(secretKey.getBytes());
            impi.setAuth_scheme(auth_scheme);
            impi.setIp("");
            impi.setAmf(HexCodec.decode(amf));
            impi.setOp(HexCodec.decode(op));
            impi.setOpc(HexCodec.decode(opc));
            impi.setSqn("000000000000");
            impi.setId_imsu(-1);
            impi.setDefault_auth_scheme(1);
            impi.setLine_identifier("");
            IMPI_DAO.insert(session, impi);
            id = impi.getId();
            System.out.println("Added IMPI: " + identity); 
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
        return id;
    }

    public static int createIMPU(String[] args) {
        return 0;
        
    }
}

