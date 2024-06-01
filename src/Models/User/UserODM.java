package Models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserODM extends UserModel {
    public String nombre;
    public String email;
    public String password;
    public String rol;
    public String create_at;
    public String update_at;

    public UserODM(String nombre, String email, String password, String rol) {
        super();
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

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
     * @return the rol
     */
    public String getRol() {
        return rol;
    }

    /**
     * @param rol the rol to set
     */
    public void setRol(String rol) {
        this.rol = rol;
    }
    public String getCreate_at() {
        return create_at;
    }
    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }
    public String getUpdate_at() {
        return update_at;
    }
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
