package com.conectatec.di;

import com.conectatec.data.repository.AuthRepository;
import com.conectatec.data.repository.AuthRepositoryImpl;
import com.conectatec.data.repository.ChatRepository;
import com.conectatec.data.repository.ChatRepositoryImpl;
import com.conectatec.data.repository.DashboardRepository;
import com.conectatec.data.repository.DashboardRepositoryImpl;
import com.conectatec.data.repository.EstudianteRepository;
import com.conectatec.data.repository.EstudianteRepositoryImpl;
import com.conectatec.data.repository.GruposRepository;
import com.conectatec.data.repository.GruposRepositoryImpl;
import com.conectatec.data.repository.TareasRepository;
import com.conectatec.data.repository.TareasRepositoryImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds @Singleton
    abstract GruposRepository bindGruposRepository(GruposRepositoryImpl impl);

    @Binds @Singleton
    abstract TareasRepository bindTareasRepository(TareasRepositoryImpl impl);

    @Binds @Singleton
    abstract ChatRepository bindChatRepository(ChatRepositoryImpl impl);

    @Binds @Singleton
    abstract DashboardRepository bindDashboardRepository(DashboardRepositoryImpl impl);

    @Binds @Singleton
    abstract AuthRepository bindAuthRepository(AuthRepositoryImpl impl);

    @Binds @Singleton
    abstract EstudianteRepository bindEstudianteRepository(EstudianteRepositoryImpl impl);
}
