create table if not exists configs (
    id int auto_increment primary key,
    app varchar(64) not null,
    env varchar(64) not null,
    ns varchar(64) not null,
    properties varchar(64) not null,
    placeholder varchar(128) null
);

insert into configs (app,env,ns,properties,placeholder)values ('app1', 'dev', 'public', 'cz.config.ns', 'db-init-ns1');
insert into configs (app,env,ns,properties,placeholder)values ('app1', 'dev', 'public', 'cz.config.app', 'db-init-server');
insert into configs (app,env,ns,properties,placeholder)values ('app1', 'dev', 'public', 'cz.config.env', 'db-init-dev');