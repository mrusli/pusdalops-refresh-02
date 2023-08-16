package mil.pusdalops.domain.kejadian;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import mil.pusdalops.domain.common.IdBasedObject;
import mil.pusdalops.domain.common.SchemaUtil;

/**
 * 
 * @author rusli
 *
 */
@Entity
@Table(name = "kejadian_jenis", schema = SchemaUtil.SCHEMA_COMMON)
public class KejadianJenis extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8143331190124174052L;

	//  `jenis` varchar(255) DEFAULT NULL,
	@Column(name = "jenis")
	private String namaJenis;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "KejadianJenis [id=" + super.getId() + ", namaJenis=" + namaJenis + "]";
	}

	public String getNamaJenis() {
		return namaJenis;
	}

	public void setNamaJenis(String namaJenis) {
		this.namaJenis = namaJenis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(namaJenis);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		KejadianJenis other = (KejadianJenis) obj;
		return Objects.equals(namaJenis, other.namaJenis);
	}
	
	
}
