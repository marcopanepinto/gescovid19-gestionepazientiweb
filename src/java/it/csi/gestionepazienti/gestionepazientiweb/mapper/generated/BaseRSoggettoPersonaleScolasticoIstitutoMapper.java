package it.csi.gestionepazienti.gestionepazientiweb.mapper.generated;

import it.csi.gestionepazienti.gestionepazientiweb.dto.RSoggettoPersonaleScolasticoIstituto;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

public interface BaseRSoggettoPersonaleScolasticoIstitutoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table r_soggetto_personale_scolastico_istituto
     *
     * @mbg.generated
     */
    @Delete({
        "delete from r_soggetto_personale_scolastico_istituto",
        "where sogperscol_ist_id = #{sogperscolIstId,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer sogperscolIstId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table r_soggetto_personale_scolastico_istituto
     *
     * @mbg.generated
     */
    @Insert({
        "insert into r_soggetto_personale_scolastico_istituto (sogperscol_ist_id, id_soggetto, ",
        "id_istituto, validita_inizio, ",
        "validita_fine, utente_operazione, ",
        "data_creazione, data_modifca, ",
        "data_cancellazione)",
        "values (nextval('r_soggetto_personale_scolastico_istituto_sogperscol_ist_id_seq'), #{idSoggetto,jdbcType=INTEGER}, ",
        "#{idIstituto,jdbcType=INTEGER}, #{validitaInizio,jdbcType=TIMESTAMP}, ",
        "#{validitaFine,jdbcType=TIMESTAMP}, #{utenteOperazione,jdbcType=VARCHAR}, ",
        "#{dataCreazione,jdbcType=TIMESTAMP}, #{dataModifca,jdbcType=TIMESTAMP}, ",
        "#{dataCancellazione,jdbcType=TIMESTAMP})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "sogperscolIstId")
    int insert(RSoggettoPersonaleScolasticoIstituto record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table r_soggetto_personale_scolastico_istituto
     *
     * @mbg.generated
     */
    @Select({
        "select",
        "sogperscol_ist_id, id_soggetto, id_istituto, validita_inizio, validita_fine, ",
        "utente_operazione, data_creazione, data_modifca, data_cancellazione",
        "from r_soggetto_personale_scolastico_istituto",
        "where sogperscol_ist_id = #{sogperscolIstId,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="sogperscol_ist_id", property="sogperscolIstId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="id_soggetto", property="idSoggetto", jdbcType=JdbcType.INTEGER),
        @Result(column="id_istituto", property="idIstituto", jdbcType=JdbcType.INTEGER),
        @Result(column="validita_inizio", property="validitaInizio", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="validita_fine", property="validitaFine", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="utente_operazione", property="utenteOperazione", jdbcType=JdbcType.VARCHAR),
        @Result(column="data_creazione", property="dataCreazione", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="data_modifca", property="dataModifca", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="data_cancellazione", property="dataCancellazione", jdbcType=JdbcType.TIMESTAMP)
    })
    RSoggettoPersonaleScolasticoIstituto selectByPrimaryKey(Integer sogperscolIstId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table r_soggetto_personale_scolastico_istituto
     *
     * @mbg.generated
     */
    @Select({
        "select",
        "sogperscol_ist_id, id_soggetto, id_istituto, validita_inizio, validita_fine, ",
        "utente_operazione, data_creazione, data_modifca, data_cancellazione",
        "from r_soggetto_personale_scolastico_istituto"
    })
    @Results({
        @Result(column="sogperscol_ist_id", property="sogperscolIstId", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="id_soggetto", property="idSoggetto", jdbcType=JdbcType.INTEGER),
        @Result(column="id_istituto", property="idIstituto", jdbcType=JdbcType.INTEGER),
        @Result(column="validita_inizio", property="validitaInizio", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="validita_fine", property="validitaFine", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="utente_operazione", property="utenteOperazione", jdbcType=JdbcType.VARCHAR),
        @Result(column="data_creazione", property="dataCreazione", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="data_modifca", property="dataModifca", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="data_cancellazione", property="dataCancellazione", jdbcType=JdbcType.TIMESTAMP)
    })
    List<RSoggettoPersonaleScolasticoIstituto> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table r_soggetto_personale_scolastico_istituto
     *
     * @mbg.generated
     */
    @Update({
        "update r_soggetto_personale_scolastico_istituto",
        "set id_soggetto = #{idSoggetto,jdbcType=INTEGER},",
          "id_istituto = #{idIstituto,jdbcType=INTEGER},",
          "validita_inizio = #{validitaInizio,jdbcType=TIMESTAMP},",
          "validita_fine = #{validitaFine,jdbcType=TIMESTAMP},",
          "utente_operazione = #{utenteOperazione,jdbcType=VARCHAR},",
          "data_creazione = #{dataCreazione,jdbcType=TIMESTAMP},",
          "data_modifca = #{dataModifca,jdbcType=TIMESTAMP},",
          "data_cancellazione = #{dataCancellazione,jdbcType=TIMESTAMP}",
        "where sogperscol_ist_id = #{sogperscolIstId,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(RSoggettoPersonaleScolasticoIstituto record);
}