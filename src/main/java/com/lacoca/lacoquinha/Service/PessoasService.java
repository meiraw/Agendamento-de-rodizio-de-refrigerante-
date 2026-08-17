package com.lacoca.lacoquinha.Service;

import com.lacoca.lacoquinha.DTO.RequestDTO.PessoasRequestDTO;
import com.lacoca.lacoquinha.Exception.ResourceNotFoundException;
import com.lacoca.lacoquinha.Model.PessoasModel;
import com.lacoca.lacoquinha.Repository.PessoasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PessoasService {

    private static final long TAMANHO_MAXIMO_FOTO = 5 * 1024 * 1024;

    private static final Map<String, String> EXTENSOES_PERMITIDAS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    @Autowired
    private PessoasRepository pessoasrepository;

    @Value("${app.fotos.dir}")
    private String diretorioFotosConfigurado;

    public PessoasModel salvar ( PessoasRequestDTO dto){

        PessoasModel pessoas = new PessoasModel();
        pessoas.setNome(dto.getNome());
        pessoas.setApelido(dto.getApelido());
        return pessoasrepository.save(pessoas);
    }

    public List<PessoasModel> listarpessoas (){
        return pessoasrepository.findAll();
    }

    public PessoasModel buscarPorId (UUID id){
        return pessoasrepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("O id "+id+" não foi encontrado!"));
    }


    public PessoasModel atualizar (PessoasRequestDTO dto , UUID id ){
        PessoasModel novapessoa = buscarPorId(id);
        novapessoa.setNome(dto.getNome());
        novapessoa.setApelido(dto.getApelido());
        return pessoasrepository.save(novapessoa);
    }

    public PessoasModel salvarFoto(UUID id, MultipartFile foto) {
        PessoasModel pessoa = buscarPorId(id);
        validarFoto(foto);

        String tipo = obterTipoDaFoto(foto);
        String nomeNovoArquivo = UUID.randomUUID() + EXTENSOES_PERMITIDAS.get(tipo);
        Path diretorio = obterDiretorioFotos();
        Path destino = diretorio.resolve(nomeNovoArquivo).normalize();

        if (!destino.startsWith(diretorio)) {
            throw new IllegalArgumentException("Nome de arquivo inválido.");
        }

        try {
            Files.createDirectories(diretorio);

            try (InputStream input = foto.getInputStream()) {
                Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Não foi possível salvar a foto.", ex);
        }

        String fotoAnterior = pessoa.getFotoNome();
        pessoa.setFotoNome(nomeNovoArquivo);

        PessoasModel pessoaAtualizada = pessoasrepository.save(pessoa);
        excluirArquivoSilenciosamente(fotoAnterior);

        return pessoaAtualizada;
    }

    public FotoArquivo carregarFoto(UUID id) {
        PessoasModel pessoa = buscarPorId(id);

        if (pessoa.getFotoNome() == null) {
            throw new ResourceNotFoundException("Esta pessoa não possui foto.");
        }

        Path arquivo = obterDiretorioFotos().resolve(pessoa.getFotoNome()).normalize();

        if (!arquivo.startsWith(obterDiretorioFotos())) {
            throw new ResourceNotFoundException("Foto não encontrada.");
        }

        try {
            Resource recurso = new UrlResource(arquivo.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("Foto não encontrada.");
            }

            return new FotoArquivo(recurso, obterMediaType(pessoa.getFotoNome()));
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("Foto não encontrada.");
        }
    }


    public void deletar (UUID id){
        PessoasModel excluir = buscarPorId (id);
        pessoasrepository.delete(excluir);
    }

    private void validarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("Selecione uma foto.");
        }

        if (foto.getSize() > TAMANHO_MAXIMO_FOTO) {
            throw new IllegalArgumentException("A foto deve ter no máximo 5 MB.");
        }

        String tipo = obterTipoDaFoto(foto);

        if (!EXTENSOES_PERMITIDAS.containsKey(tipo)) {
            throw new IllegalArgumentException("Formato inválido. Envie uma imagem JPG, PNG ou WEBP.");
        }
    }

    private Path obterDiretorioFotos() {
        return Paths.get(diretorioFotosConfigurado)
                .toAbsolutePath()
                .normalize();
    }

    private MediaType obterMediaType(String nomeArquivo) {
        if (nomeArquivo.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }

        if (nomeArquivo.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }

        return MediaType.IMAGE_JPEG;
    }

    private void excluirArquivoSilenciosamente(String nomeArquivo) {
        if (nomeArquivo == null) {
            return;
        }

        try {
            Files.deleteIfExists(obterDiretorioFotos().resolve(nomeArquivo).normalize());
        } catch (IOException ignored) {
        }
    }

    public record FotoArquivo(Resource arquivo, MediaType tipoConteudo) {
    }

    private String obterTipoDaFoto(MultipartFile foto) {
        String tipo = Optional.ofNullable(foto.getContentType())
                .orElse("")
                .toLowerCase(Locale.ROOT);

        if (EXTENSOES_PERMITIDAS.containsKey(tipo)) {
            return tipo;
        }

        String nomeArquivo = Optional.ofNullable(foto.getOriginalFilename())
                .orElse("")
                .toLowerCase(Locale.ROOT);

        if (nomeArquivo.endsWith(".jpg") || nomeArquivo.endsWith(".jpeg")) {
            return "image/jpeg";
        }

        if (nomeArquivo.endsWith(".png")) {
            return "image/png";
        }

        if (nomeArquivo.endsWith(".webp")) {
            return "image/webp";
        }

        return tipo;
    }
}
