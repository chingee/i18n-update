package chingee.springboot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import chingee.springboot.bean.I18n;

@Mapper
public interface I18nMapper {

	@Select("select * from I18n")
	public List<I18n> selectI18ns();
	
	@Select("select * from I18n where id = #{id}")
	public I18n selectI18n(Integer id);
	
	@Select("select * from I18n where filepath = #{filepath}")
	public List<I18n> selectI18nByFilepath(String filepath);
	
	@Insert("insert into i18n (jar,filepath,keyone,en,zh) VALUES (#{jar},#{filepath},#{keyone},#{en},#{zh})")
	public int insertI18n(I18n i18n);
	
	@Select("select distinc filepath from I18n")
	public List<String> selectPropertiesPaths();
	
}
