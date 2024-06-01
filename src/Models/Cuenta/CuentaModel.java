package Models.Cuenta;

import ORM.Utils.Formats.UsableMethods;
import ORM.Utils.Model.TableData;

public class CuentaModel implements UsableMethods {
    @TableData(constraint = "not null unique primary key auto_increment", type = "int")
    private int id_pk;
    @TableData(constraint = "not null", type = "varchar(100)")
    private String nombre;
    @TableData(constraint = "not null", type = "varchar(100)")
    private String email;
    @TableData(
        constraint = "not null. foreign key(user_id_fk) references `user`(id_pk) on delete cascade on update cascade",
        type = "int"
    )
    private int user_id_fk;
    @TableData(constraint = "not null", type = "varchar(100)")
    private String password;
    @TableData(constraint = "not null", type = "datetime")
    private String create_at;
    @TableData(constraint = "", type = "datetime")
    private String update_at;
    public CuentaModel() { }
    /**
     * @return the id_pk
     */
    public int getId_pk() {
        return id_pk;
    }
    /**
     * @param id_pk the id_pk to set
     */
    public void setId_pk(int id_pk) {
        this.id_pk = id_pk;
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
}
