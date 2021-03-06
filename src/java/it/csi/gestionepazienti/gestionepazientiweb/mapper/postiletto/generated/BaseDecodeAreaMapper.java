package it.csi.gestionepazienti.gestionepazientiweb.mapper.postiletto.generated;

import it.csi.gestionepazienti.gestionepazientiweb.dto.postiletto.DecodeArea;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;

public interface BaseDecodeAreaMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table d_area
	 * @mbg.generated
	 */
	@Delete({ "delete from d_area", "where id_d_area = #{idDArea,jdbcType=VARCHAR}" })
	int deleteByPrimaryKey(String idDArea);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table d_area
	 * @mbg.generated
	 */
	@Insert({ "insert into d_area (id_d_area, nome)",
			"values (#{idDArea,jdbcType=VARCHAR}, #{nome,jdbcType=VARCHAR})" })
	int insert(DecodeArea record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table d_area
	 * @mbg.generated
	 */
	@Select({ "select", "id_d_area, nome", "from d_area", "where id_d_area = #{idDArea,jdbcType=VARCHAR}" })
	@Results({ @Result(column = "id_d_area", property = "idDArea", jdbcType = JdbcType.VARCHAR, id = true),
			@Result(column = "nome", property = "nome", jdbcType = JdbcType.VARCHAR) })
	DecodeArea selectByPrimaryKey(String idDArea);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table d_area
	 * @mbg.generated
	 */
	@Select({ "select", "id_d_area, nome", "from d_area" })
	@Results({ @Result(column = "id_d_area", property = "idDArea", jdbcType = JdbcType.VARCHAR, id = true),
			@Result(column = "nome", property = "nome", jdbcType = JdbcType.VARCHAR) })
	List<DecodeArea> selectAll();

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table d_area
	 * @mbg.generated
	 */
	@Update({ "update d_area", "set nome = #{nome,jdbcType=VARCHAR}", "where id_d_area = #{idDArea,jdbcType=VARCHAR}" })
	int updateByPrimaryKey(DecodeArea record);
}