CREATE TABLE application (
	id SERIAL NOT NULL, 
	key_id VARCHAR(255), 
	key_secret VARCHAR(255), 
	name VARCHAR(255), 
	redirect_uri VARCHAR(255), 
	time_out VARCHAR(255), 
	x_access BOOLEAN, 
	PRIMARY KEY (id)
);

CREATE TABLE application_fi_scopes (
  application_id BIGINT NOT NULL,
  fournisseur_identite VARCHAR(255), 
  scopes VARCHAR(9999)
);
ALTER TABLE application_fi_scopes ADD CONSTRAINT FK_fi_scopes_app FOREIGN KEY (application_id) REFERENCES application (id) on delete cascade;

CREATE SEQUENCE code_id_seq INCREMENT 1 START 1 CYCLE;
CREATE TABLE code (
	id int8 NOT NULL default nextval('code_id_seq'),
	access_token VARCHAR(255), 
	code VARCHAR(255), 
	PRIMARY KEY (id)
);

CREATE SEQUENCE idtoken_id_seq INCREMENT 1 START 1 CYCLE;
CREATE TABLE idtoken (
	id int8 NOT NULL default nextval('idtoken_id_seq'),
	access_token VARCHAR(255), 
	aud VARCHAR(255), 
	email VARCHAR(255), 
	exp VARCHAR(255), 
	iat VARCHAR(255), 
	iss VARCHAR(255), 
	nom VARCHAR(255), 
	prenom VARCHAR(255), 
	sub VARCHAR(255), 
	PRIMARY KEY (id)
);

CREATE TABLE idtoken_scope_data (
  idtoken_id BIGINT NOT NULL,
  key VARCHAR(255), 
  value VARCHAR(255)
);
ALTER TABLE idtoken_scope_data ADD CONSTRAINT FK_scope_data_id_token FOREIGN KEY (idtoken_id) REFERENCES idtoken (id) on delete cascade;

CREATE SEQUENCE token_id_seq INCREMENT 1 START 1 CYCLE;
CREATE TABLE token (
	id int8 NOT NULL default nextval('token_id_seq'),
	access_token VARCHAR(255),
	exp VARCHAR(255),
	id_token VARCHAR(9999),
	provider VARCHAR(255), 
	token_type VARCHAR(255), 
	PRIMARY KEY (id)
);