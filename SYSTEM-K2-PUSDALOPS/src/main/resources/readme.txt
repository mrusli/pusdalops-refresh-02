15/07/2022 - verion 1.0.12
- Allow clients to download exported report output file from server
	* remove filename dialog
	* server picks up directory to save the exported report file from application-back-end.properties file:
		- output.file.dir=/pusdalops/doc/
	* sever saves the exported report file in the server (needs to clean-up periodically)
	* after the file is exported and save in the server directory, client will receive the downloaded file
		- client save directory depends on the browser settings (usually it's c:\Users\[username\Downloads
- Check the followings when deploy to server
	* Add the output directory for the ms doc export file:
		output.file.dir=/pusdalops/doc/	
11/07/2022 - version 1.0.11
- Update Laporan | Cetak Laporan Rutin
	* Fix the ways to display the Kejadian
	* Improve the Jenis Kejadian selections
	* Update the onSelect Jenis Kejadian for both display and export
	* Allows export to MS Word
	* Update log4j to allow logging save to file in the server
	* Use jasperreports designer to create report for export
- Check the followings when deploy to server
	* Check / Update the followings in the /pusdalops/application-back-end.properties:
		matra_darat.id=ID
		matra_laut.id=ID
		matra_udara.id=ID
	where ID is the ID from the Kotamaops table (check the Kotamaops table from MySQL Workbench)
	* Add the output directory for the ms doc export file:
		output.file.dir=C:\\pusdalops\\doc\\
	* Add the followings in the Server:
		- /pusdalops/jasper/Laporan_Rutin_A4.jasper
	* Check / Update the /pusdalops/hibernate.properties:
		- hibernate.connection.url=jdbc:mysql://[databaseserver_ip]:3306/e010_k2_pusdalops?autoReconnect=true&useSSL=false
		Note: the hibernate.properties in the application can point to localhost (I think), give a try !!!
	* Create the 'doc' directory
- The output of the doc file will be in the client's local directory:
	- determine by the export file prompt, but usually it's 'C:\pusdalops\doc'
	- the out file is 'Laporan_Rutin_' and posfix by year,month,day,hour,minute,second 
	  during the export file prompt
- Update jasperreports maven to version 6.14.0
27/06/2022 - version 1.0.10
- Add new data entry form to include
	* search by TW Awal/Akhir
	* search by Id (wildcard)
	* search by text
	* sort by twPembuatan / twKejadian
	* input the Kerugian in the same form
- Add search and sort in Kejadian Menonjol
	* search by Id (wildcard)
	* sort by Id, twPembuatan, twKejadian
- Add re-index database in the Profil | Koneksi Database page
	* add a button to re-index database
- Remove all re-index after add, edit, or delete
- Allow Hibernate Search to update the index everytime an object is inserted, updated or deleted through Hibernate
	* remove the manual indexing strategy from hibernate properties
17/11/2021 - version 1.0.9
- Add matra to Laporan Rutin
- Add Jenis Kejadian filter to Laporan Rutin
Instruction: User starts by choosing the 'Matra'.  After the system lists the Kejadian, the Jenis Kejadian of the Kejadian is populated.
Deployment:
- Add the followings as Kotamaops from Settings | Kotamaops menu:
* Angkatan Darat
* Angkatan Laut
* Angkatan Udara
- In the database, find the kotamaops_join_kotamaops table
- Join at least one kotamaops for each of the angkatan
- Note the ID for each angkatan from the table
- Adds the followings to the \pusdalops\application-back-end.properties:
matra_darat.id=ID
matra_laut.id=ID
matra_udara.id=ID
where ID is the ID from the Kotamaops table
- Add the following kotamaops to the kotamaops table using menu Settings | Kotamaops:
KORMAR
KOOPSAU I
KOOPSAU II
KOOPSAU III
KOHANUDNAS I
- Add sequence for the kotamaops.  Refer to \documents\pusdalops-kotamaops-List.txt (in onedrive)
- Use the sql commands to insert the sequence.  Refer to \documents\pusdalops-kotamaops-List.txt (in onedrive)
- List of kotamaops in comboboxes sorted according to sequence