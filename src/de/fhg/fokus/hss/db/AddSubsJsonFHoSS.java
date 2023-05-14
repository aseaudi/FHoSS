package de.fhg.fokus.hss.db;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import de.fhg.fokus.hss.db.model.IMPI;
import de.fhg.fokus.hss.db.model.IMSU;
import de.fhg.fokus.hss.db.op.IMPI_DAO;
import de.fhg.fokus.hss.db.op.IMSU_DAO;
import de.fhg.fokus.hss.db.hibernate.*;
import de.fhg.fokus.hss.auth.HexCodec;
import de.fhg.fokus.hss.cx.CxConstants;
import de.fhg.fokus.hss.db.model.IMPU;
import de.fhg.fokus.hss.db.model.IMPU_VisitedNetwork;
import de.fhg.fokus.hss.db.op.IMPI_IMPU_DAO;
import de.fhg.fokus.hss.db.op.IMPU_DAO;
import de.fhg.fokus.hss.db.op.IMPU_VisitedNetwork_DAO;
import de.fhg.fokus.hss.zh.ZhConstants;

// $JAVA_HOME/bin/java -cp $CLASSPATH de.fhg.fokus.hss.web.action.AddSubsFHoSS <id> <k> <amf> <op> <opc> <domain> <msisdn>

// $JAVA_HOME/bin/java -cp $CLASSPATH de.fhg.fokus.hss.web.action.AddSubsFHoSS 001010000000001 00000001 ims.mnc001.mcc001.3ggnetwork.org fec86ba6eb707ed08905757b1bb44b8f 8000 00000000000000000000000000000000 c42449363bbad02b66d16bc975d77cc1

class AddSubsJsonFHoSS {
    private static Logger logger = Logger.getLogger(AddSubsFHoSS.class);
    public static void main(String[] args) {
        System.out.println("Adding Subscribers to FHoSS ..."); 
        JSONArray subsJsonArray = readSubsJson();
        for (Object sub : subsJsonArray) {
            JSONObject subJson = (JSONObject) sub;
            int imsu_id = createIMSU(subJson);
            // int ipmi_id = createIMPI(subJson, imsu_id);
            // int impu1_id = createIMPU(subJson, "sip:" + subJson.get("id"), ipmi_id, 0);
            // int impu2_id = createIMPU(subJson, "sip:" + subJson.get("id").substring(5), ipmi_id, impu1_id);
            // int impu3_id = createIMPU(subJson, "tel:" + subJson.get("msisdn"), ipmi_id, impu1_id);
        }

    }

    public static JSONArray readSubsJson() {
        JSONParser jsonParser = new JSONParser();
        JSONObject subsJson = null;
        try {
            subsJson = (JSONObject) jsonParser.parse(new FileReader("subscribers.json"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JSONArray subsJsonArray = (JSONArray) subsJson.get("Subscribers");
        return subsJsonArray;
    }
    public static int createIMSU(JSONObject subJson) {
        int id = -1;
        boolean dbException = false;
        try{
            Session session = HibernateUtil.getCurrentSession();
            HibernateUtil.beginTransaction();
            IMSU imsu = null;
            imsu = new IMSU();
            imsu.setName((String) subJson.get("id"));
            imsu.setDiameter_name("");
            imsu.setScscf_name("");
            imsu.setId_capabilities_set(1);
            imsu.setId_preferred_scscf_set(1);
            IMSU_DAO.insert(session, imsu);
            id = imsu.getId();
            System.out.println("Added IMSU: " + (String) subJson.get("id")); 
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

    public static int createIMPI(String[] args, int imsu_id) {
        int id = -1;
        boolean dbException = false;
        try{
            Session session = HibernateUtil.getCurrentSession();
            HibernateUtil.beginTransaction();
            int auth_scheme = 255;
            IMPI impi;
            String identity = args[0] + "@" + args[2];
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
            impi.setId_imsu(imsu_id);
            IMPI_DAO.update(session, impi);
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

    public static int createIMPU(String[] args, String impu_id, Integer impi_id, Integer impu1_id) {
        int id = -1;
        boolean dbException = false;
        try{
            Session session = HibernateUtil.getCurrentSession();
            HibernateUtil.beginTransaction();
            IMPU impu = null;
            String identity = impu_id + "@" + args[2];
            impu = new IMPU();
            impu.setIdentity(identity);
            impu.setBarring(1);
            impu.setType(0);				
            impu.setWildcard_psi("");
            impu.setPsi_activation(0);
            impu.setDisplay_name("");
            impu.setCan_register(1);
            impu.setId_sp(1);
            impu.setId_charging_info(1);
            IMPU_DAO.insert(session, impu);
            if (impu1_id == 0){
                impu.setBarring(1);
                impu.setId_implicit_set(impu.getId());
            }
            else {
                impu.setBarring(0);
                impu.setId_implicit_set(impu1_id);
            }
            IMPU_DAO.update(session, impu);
            id = impu.getId();
            IMPU_VisitedNetwork impu_vn = new IMPU_VisitedNetwork();
            impu_vn.setId_impu(id);
            impu_vn.setId_visited_network(1);
            IMPU_VisitedNetwork_DAO.insert(session, impu_vn);
            IMPI_IMPU_DAO.insert(session, impi_id, id, CxConstants.IMPU_user_state_Not_Registered);
            System.out.println("Added IMPU: " + identity); 
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
}

