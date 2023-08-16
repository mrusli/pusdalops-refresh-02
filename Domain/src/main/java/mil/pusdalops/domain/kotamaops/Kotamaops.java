package mil.pusdalops.domain.kotamaops;

import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import mil.pusdalops.domain.common.IdBasedObject;
import mil.pusdalops.domain.common.SchemaUtil;
import mil.pusdalops.domain.gmt.TimezoneInd;
import mil.pusdalops.domain.personel.Personel;
import mil.pusdalops.domain.wilayah.Propinsi;

@Entity
@Table(name = "kotamaops", schema = SchemaUtil.SCHEMA_COMMON)
public class Kotamaops extends IdBasedObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6832943931715238645L;

	//  `type` int(11) DEFAULT NULL,
	@Column(name = "type")
	@Enumerated(EnumType.ORDINAL)
	private KotamaopsType kotamaopsType;
	
	//  `legal_name` varchar(255) DEFAULT NULL,
	@Column(name = "legal_name")
	private String kotamaopsName;
	
	//  `display_name` varchar(255) DEFAULT NULL,
	@Column(name = "display_name")
	private String kotamaopsDisplayName;
	
	@Column(name = "image_id")
	private String imagedId;

	//	`image_id_01` VARCHAR(225) NULL
	@Column(name = "image_id_01")
	private String imageId01;
	
	//  `address_01` varchar(255) DEFAULT NULL,
	@Column(name = "address_01")
	private String address01;
	
	//  `address_02` varchar(255) DEFAULT NULL,
	@Column(name = "address_02")
	private String address02;
	
	//  `city` varchar(255) DEFAULT NULL,
	@Column(name = "city")
	private String city;
	
	//  `postal_code` varchar(255) DEFAULT NULL,
	@Column(name = "postal_code")
	private String postalCode;
	
	//  `phone` varchar(255) DEFAULT NULL,
	@Column(name = "phone")
	private String phone;
	
	//  `email` varchar(255) DEFAULT NULL,
	@Column(name = "email")
	private String email;
	
	//  `fax` varchar(255) DEFAULT NULL,
	@Column(name = "fax")
	private String fax;
	
	//  `timezone` int(11) DEFAULT NULL,
	@Column(name = "timezone")
	@Enumerated(EnumType.ORDINAL)
	private TimezoneInd timeZone;
	
	// `doc_code` varchar(4)
	@Column(name = "doc_code")
	private String documentCode;

	//	location_enum int(11)
	@Column(name = "location_enum")
	private int kotamaopsSequence;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
	@JoinTable(
			name = "kotamaops_join_personel",
			joinColumns = @JoinColumn(name = "id_kotamaops"),
			inverseJoinColumns = @JoinColumn(name = "id_personel"))
	private List<Personel> personels;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval=false, fetch=FetchType.LAZY)
	@JoinTable(
			name = "kotamaops_join_propinsi",
			joinColumns = @JoinColumn(name = "id_kotamaops"),
			inverseJoinColumns = @JoinColumn(name = "id_propinsi"))
	private List<Propinsi> propinsis;

	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
	@JoinTable(
			name = "kotamaops_join_kotamaops",
			joinColumns = @JoinColumn(name = "id_kotamaops_m"),
			inverseJoinColumns = @JoinColumn(name = "id_kotamaops_d"))
	@OrderBy("kotamaopsSequence")
	private List<Kotamaops> kotamaops;
	
	@Override
	public String toString() {
		return "Kotamaops [id=" + super.getId() + ", kotamaopsType=" + kotamaopsType + ", kotamaopsName=" + kotamaopsName
				+ ", kotamaopsDisplayName=" + kotamaopsDisplayName + ", imagedId=" + imagedId + ", imageId01="
				+ imageId01 + ", address01=" + address01 + ", address02=" + address02 + ", city=" + city
				+ ", postalCode=" + postalCode + ", phone=" + phone + ", email=" + email + ", fax=" + fax
				+ ", timeZone=" + timeZone + ", documentCode=" + documentCode + "]";
	}

	/**
	 * @return the kotamaopsType
	 */
	public KotamaopsType getKotamaopsType() {
		return kotamaopsType;
	}

	/**
	 * @param kotamaopsType the kotamaopsType to set
	 */
	public void setKotamaopsType(KotamaopsType kotamaopsType) {
		this.kotamaopsType = kotamaopsType;
	}

	/**
	 * @return the kotamaopsName
	 */
	public String getKotamaopsName() {
		return kotamaopsName;
	}

	/**
	 * @param kotamaopsName the kotamaopsName to set
	 */
	public void setKotamaopsName(String kotamaopsName) {
		this.kotamaopsName = kotamaopsName;
	}

	/**
	 * @return the kotamaopsDisplayName
	 */
	public String getKotamaopsDisplayName() {
		return kotamaopsDisplayName;
	}

	/**
	 * @param kotamaopsDisplayName the kotamaopsDisplayName to set
	 */
	public void setKotamaopsDisplayName(String kotamaopsDisplayName) {
		this.kotamaopsDisplayName = kotamaopsDisplayName;
	}

	/**
	 * @return the address01
	 */
	public String getAddress01() {
		return address01;
	}

	/**
	 * @param address01 the address01 to set
	 */
	public void setAddress01(String address01) {
		this.address01 = address01;
	}

	/**
	 * @return the address02
	 */
	public String getAddress02() {
		return address02;
	}

	/**
	 * @param address02 the address02 to set
	 */
	public void setAddress02(String address02) {
		this.address02 = address02;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
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
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	public List<Personel> getPersonels() {
		return personels;
	}

	public void setPersonels(List<Personel> personels) {
		this.personels = personels;
	}

	public List<Propinsi> getPropinsis() {
		return propinsis;
	}

	public void setPropinsis(List<Propinsi> propinsis) {
		this.propinsis = propinsis;
	}

	public String getImagedId() {
		return imagedId;
	}

	public void setImagedId(String imagedId) {
		this.imagedId = imagedId;
	}

	public TimezoneInd getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimezoneInd timeZone) {
		this.timeZone = timeZone;
	}

	public List<Kotamaops> getKotamaops() {
		return kotamaops;
	}

	public void setKotamaops(List<Kotamaops> kotamaops) {
		this.kotamaops = kotamaops;
	}

	public String getDocumentCode() {
		return documentCode;
	}

	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}

	public String getImageId01() {
		return imageId01;
	}

	public void setImageId01(String imageId01) {
		this.imageId01 = imageId01;
	}

	public int getKotamaopsSequence() {
		return kotamaopsSequence;
	}

	public void setKotamaopsSequence(int kotamaopsSequence) {
		this.kotamaopsSequence = kotamaopsSequence;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(address01, address02, city, documentCode, email, fax, imageId01,
				imagedId, kotamaops, kotamaopsDisplayName, kotamaopsName, kotamaopsSequence, kotamaopsType, personels,
				phone, postalCode, propinsis, timeZone);
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
		Kotamaops other = (Kotamaops) obj;
		return Objects.equals(address01, other.address01) && Objects.equals(address02, other.address02)
				&& Objects.equals(city, other.city) && Objects.equals(documentCode, other.documentCode)
				&& Objects.equals(email, other.email) && Objects.equals(fax, other.fax)
				&& Objects.equals(imageId01, other.imageId01) && Objects.equals(imagedId, other.imagedId)
				&& Objects.equals(kotamaops, other.kotamaops)
				&& Objects.equals(kotamaopsDisplayName, other.kotamaopsDisplayName)
				&& Objects.equals(kotamaopsName, other.kotamaopsName) && kotamaopsSequence == other.kotamaopsSequence
				&& kotamaopsType == other.kotamaopsType && Objects.equals(personels, other.personels)
				&& Objects.equals(phone, other.phone) && Objects.equals(postalCode, other.postalCode)
				&& Objects.equals(propinsis, other.propinsis) && timeZone == other.timeZone;
	}
	
}
