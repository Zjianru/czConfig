create table if not exists configs (
    id int auto_increment primary key,
    app varchar(64) not null,
    env varchar(64) not null,
    ns varchar(64) not null,
    properties varchar(64) not null,
    placeholder varchar(128) null
);

insert into configs (app,env,ns,properties,placeholder)values ('app1', 'dev', 'public', 'version', '1.0');
insert into configs (app,env,ns,properties,placeholder)values ('app1', 'dev', 'public', 'cz.config.app', 'czConfigfServer');
insert into configs (app,env,ns,properties,placeholder)values ('app1', 'dev', 'public', 'cz.config.env', 'dev');