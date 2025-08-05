package com.bancobr.config;

import com.bancobr.model.Usuario;
import com.bancobr.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/**
 * Inicializador do banco de dados
 * 
 * @author Sistema Bancário
 * @version 1.0.0
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se já existem usuários
        if (usuarioRepository.count() == 0) {
            System.out.println("🌱 Inicializando banco de dados com dados de teste...");
            
            // Criar usuários de teste
            criarUsuariosTeste();
            
            System.out.println("✅ Banco de dados inicializado com sucesso!");
        } else {
            System.out.println("ℹ️  Banco de dados já possui dados, pulando inicialização.");
        }
    }

    private void criarUsuariosTeste() {
        // Senhas BCrypt válidas
        String admin123Hash = passwordEncoder.encode("admin123");
        String cliente123Hash = passwordEncoder.encode("cliente123");
        String cliente456Hash = passwordEncoder.encode("cliente456");
        String cliente789Hash = passwordEncoder.encode("cliente789");
        String cliente012Hash = passwordEncoder.encode("cliente012");

        // Criar usuários
        Usuario admin = new Usuario();
        admin.setId(UUID.randomUUID());
        admin.setNome("Administrador Sistema");
        admin.setEmail("admin@bancobr.com");
        admin.setSenhaHash(admin123Hash);
        admin.setCpf("00000000000");
        admin.setTipo(Usuario.TipoUsuario.ADMIN);
        admin.setAtivo(true);

        Usuario joao = new Usuario();
        joao.setId(UUID.randomUUID());
        joao.setNome("João Silva Santos");
        joao.setEmail("joao.silva@email.com");
        joao.setSenhaHash(cliente123Hash);
        joao.setCpf("11111111111");
        joao.setTipo(Usuario.TipoUsuario.CLIENTE);
        joao.setAtivo(true);

        Usuario maria = new Usuario();
        maria.setId(UUID.randomUUID());
        maria.setNome("Maria Oliveira Costa");
        maria.setEmail("maria.oliveira@email.com");
        maria.setSenhaHash(cliente456Hash);
        maria.setCpf("22222222222");
        maria.setTipo(Usuario.TipoUsuario.CLIENTE);
        maria.setAtivo(true);

        Usuario pedro = new Usuario();
        pedro.setId(UUID.randomUUID());
        pedro.setNome("Pedro Santos Lima");
        pedro.setEmail("pedro.santos@email.com");
        pedro.setSenhaHash(cliente789Hash);
        pedro.setCpf("33333333333");
        pedro.setTipo(Usuario.TipoUsuario.CLIENTE);
        pedro.setAtivo(true);

        Usuario ana = new Usuario();
        ana.setId(UUID.randomUUID());
        ana.setNome("Ana Paula Ferreira");
        ana.setEmail("ana.ferreira@email.com");
        ana.setSenhaHash(cliente012Hash);
        ana.setCpf("44444444444");
        ana.setTipo(Usuario.TipoUsuario.CLIENTE);
        ana.setAtivo(true);

        // Salvar usuários
        usuarioRepository.saveAll(Arrays.asList(admin, joao, maria, pedro, ana));
        
        System.out.println("👥 Usuários criados:");
        System.out.println("   - admin@bancobr.com (senha: admin123)");
        System.out.println("   - joao.silva@email.com (senha: cliente123)");
        System.out.println("   - maria.oliveira@email.com (senha: cliente456)");
        System.out.println("   - pedro.santos@email.com (senha: cliente789)");
        System.out.println("   - ana.ferreira@email.com (senha: cliente012)");
    }
} 