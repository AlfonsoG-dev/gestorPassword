package Models.Cuenta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CuentaODM extends CuentaModel {
    public String nombre;
    public String email;
    public int user_id_fk;
    public String password;
    public String create_at;
    public String update_at;
    
    public CuentaODM(String nombre, String email, int loggeduser, String password) {
        this.nombre = nombre;
        this.email = email;
        this.user_id_fk = loggeduser;
        this.password = password;
    }
    public CuentaODM(CuentaModel m) {
        nombre = m.getNombre();
        email = m.getEmail();
        user_id_fk = m.getUser_id_fk();
        password = m.getPassword();
        create_at = m.getCreate_at();
        update_at = m.getUpdate_at();
    }
    public CuentaODM() { }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the user_id_fk
     */
    public int getUser_id_fk() {
        return user_id_fk;
    }

    /**
     * @param user_id_fk the user_id_fk to set
     */
    public void setUser_id_fk(int user_id_fk) {
        this.user_id_fk = user_id_fk;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the create_at
     */
    public String getCreate_at() {
        return create_at;
    }

    /**
     * @param create_at the create_at to set
     */
    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    /**
     * @return the update_at
     */
    public String getUpdate_at() {
        return update_at;
    }

    /**
     * @param update_at the update_at to set
     */
    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }
    public void makeCreate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        create_at = dtf.format(miDate).toString();
    }
    public void makeUpdate_at() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime miDate = LocalDateTime.now();  
        update_at = dtf.format(miDate).toString();
    }
}
