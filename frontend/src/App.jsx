import { useCallback, useEffect, useMemo, useState } from 'react';
import { agendamentosApi, ciclosApi, fotoCompleta, pessoasApi } from './services/api';

const dias = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
const meses = ['janeiro', 'fevereiro', 'março', 'abril', 'maio', 'junho', 'julho', 'agosto', 'setembro', 'outubro', 'novembro', 'dezembro'];
const hoje = new Date().toISOString().slice(0, 10);

function dataPt(data) { return data ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'medium' }).format(new Date(`${data}T12:00:00`)) : '—'; }
function iniciais(nome = '') { return nome.split(' ').filter(Boolean).slice(0, 2).map((n) => n[0]).join('').toUpperCase() || '?'; }
function Avatar({ pessoa, pequeno = false }) {
  const foto = fotoCompleta(pessoa?.fotoUrl);
  return foto ? <img className={`avatar ${pequeno ? 'pequeno' : ''}`} src={foto} alt={`Foto de ${pessoa.nome}`} /> : <span className={`avatar sem-foto ${pequeno ? 'pequeno' : ''}`}>{iniciais(pessoa?.nome || pessoa?.pessoaNome)}</span>;
}
function Estado({ status }) { return <span className={`estado ${status?.toLowerCase()}`}>{status?.toLowerCase() || 'sem status'}</span>; }
function Vazio({ children }) { return <div className="vazio">{children}</div>; }
function Erro({ children }) { return children ? <div className="erro">{children}</div> : null; }

export default function App() {
  const [tela, setTela] = useState('dashboard');
  const [pessoas, setPessoas] = useState([]);
  const [ciclos, setCiclos] = useState([]);
  const [cicloId, setCicloId] = useState('');
  const [agendamentos, setAgendamentos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const carregarBase = useCallback(async () => {
    setCarregando(true); setErro('');
    try {
      const [listaPessoas, listaCiclos] = await Promise.all([pessoasApi.listar(), ciclosApi.listar()]);
      setPessoas(listaPessoas); setCiclos(listaCiclos);
      const atual = listaCiclos.find((c) => c.ativo && c.dataInicio <= hoje && c.dataFim >= hoje) || listaCiclos.find((c) => c.ativo) || listaCiclos[0];
      setCicloId((valor) => valor || atual?.id || '');
    } catch (e) { setErro(e.message); } finally { setCarregando(false); }
  }, []);
  useEffect(() => { carregarBase(); }, [carregarBase]);
  const carregarAgendamentos = useCallback(async () => {
    if (!cicloId) { setAgendamentos([]); return; }
    try { setAgendamentos(await agendamentosApi.listarPorCiclo(cicloId)); } catch (e) { setErro(e.message); }
  }, [cicloId]);
  useEffect(() => { carregarAgendamentos(); }, [carregarAgendamentos]);

  const ciclo = ciclos.find((item) => item.id === cicloId);
  const contexto = { pessoas, ciclos, ciclo, cicloId, agendamentos, setErro, recarregar: carregarBase, recarregarAgendamentos: carregarAgendamentos, selecionarCiclo: setCicloId };
  if (carregando) return <main className="central"><div className="spinner" />Carregando dados da API…</main>;
  const navegacao = [['dashboard', 'dashboard', 'Dashboard'], ['pessoas', 'group', 'Pessoas'], ['ciclos', 'refresh', 'Ciclos'], ['calendario', 'calendar_month', 'Calendário']];
  return <div className="app"><aside><div className="marca"><span className="logo-redondo">Co</span><div><b>La Coca</b><span>Management Portal</span></div></div><button className="novo-ciclo" onClick={() => setTela('ciclos')}><Icon nome="add" />Novo ciclo</button><nav>{navegacao.map(([id, icone, texto]) => <button key={id} className={tela === id ? 'ativo' : ''} onClick={() => setTela(id)}><Icon nome={icone} />{texto}</button>)}</nav><div className="rodape-menu"><button><Icon nome="help" />Ajuda</button></div></aside><main><header><div><p className="sobretitulo">Gestão de rodízio</p><h1>{({ dashboard: 'Dashboard', pessoas: 'Pessoas', ciclos: 'Ciclos de bebidas', calendario: 'Calendário' })[tela]}</h1><p className="subtitulo">{({ dashboard: 'Visão do ciclo atual e dos próximos pagamentos.', pessoas: 'Gerencie as pessoas que participam do rodízio.', ciclos: 'Gerencie cronogramas e participantes.', calendario: 'Acompanhe as cotas e os pagamentos do ciclo.' })[tela]}</p></div><CicloSelect {...contexto} /></header><Erro>{erro}</Erro>{tela === 'dashboard' && <Dashboard {...contexto} />}{tela === 'pessoas' && <Pessoas {...contexto} />}{tela === 'ciclos' && <Ciclos {...contexto} />}{tela === 'calendario' && <Calendario {...contexto} />}</main></div>;
}

function Icon({ nome }) { return <span className="material-symbols-outlined">{nome}</span>; }

function CicloSelect({ ciclos, cicloId, selecionarCiclo }) { return <label className="seletor">Ciclo selecionado<select value={cicloId} onChange={(e) => selecionarCiclo(e.target.value)}><option value="">Nenhum ciclo</option>{ciclos.map((c) => <option key={c.id} value={c.id}>{c.nome}{c.ativo ? ' • ativo' : ''}</option>)}</select></label>; }

function Dashboard({ ciclo, agendamentos }) {
  if (!ciclo) return <Vazio>Crie um ciclo para começar a acompanhar o rodízio.</Vazio>;
  const pendentes = agendamentos.filter((a) => a.status === 'PENDENTE');
  const proximaCota = pendentes.find((a) => a.tipo === 'COTA' && a.data >= hoje);
  const proximoPagamento = pendentes.find((a) => a.tipo === 'PAGAMENTO' && a.data >= hoje);
  const pagamentos = agendamentos.filter((a) => a.tipo === 'PAGAMENTO');
  const pagos = pagamentos.filter((a) => a.status === 'PAGO').length;
  const percentual = pagamentos.length ? Math.round((pagos / pagamentos.length) * 100) : 0;
  return <><section className="metricas"><Info titulo="Ciclo atual" valor={ciclo.nome} detalhe={ciclo.ativo ? '● Ativo' : 'Inativo'} icone="refresh" /><Info titulo="Próxima cota" valor={dataPt(proximaCota?.data)} detalhe="Segunda-feira" icone="local_drink" /><Info titulo="Próximo pagamento" valor={dataPt(proximoPagamento?.data)} detalhe="Sexta-feira" icone="payments" /><article className="card responsavel"><div className="card-cabecalho"><p>Responsável</p><Icon nome="person" /></div><div className="pessoa-responsavel">{proximoPagamento?.pessoaNome && <Avatar pessoa={{ nome: proximoPagamento.pessoaNome }} />}<div><strong>{proximoPagamento?.pessoaApelido || '—'}</strong><span>{proximoPagamento?.pessoaNome || 'Nenhum pagamento pendente'}</span></div></div></article></section><section className="dashboard-grade"><article className="painel resumo"><div className="titulo-painel"><h2>Resumo dos agendamentos</h2><span>{agendamentos.length} registros</span></div><div className="lista-resumo"><LinhaResumo icone="schedule" titulo="Pendentes" quantidade={agendamentos.filter((a) => a.status === 'PENDENTE').length} status="pendente" /><LinhaResumo icone="check_circle" titulo="Pagos" quantidade={agendamentos.filter((a) => a.status === 'PAGO').length} status="pago" /><LinhaResumo icone="event_busy" titulo="Adiados" quantidade={agendamentos.filter((a) => a.status === 'ADIADO').length} status="adiado" /></div></article><article className="painel progresso"><h2>Progresso do ciclo</h2><div className="copo"><div style={{ height: `${percentual}%` }} /></div><strong>{percentual}%</strong><span>Pagamentos concluídos</span></article></section></>;
}
function Info({ titulo, valor, detalhe, icone }) { return <article className="card"><div className="card-cabecalho"><p>{titulo}</p>{icone && <Icon nome={icone} />}</div><strong>{valor}</strong>{detalhe && <span>{detalhe}</span>}</article>; }
function LinhaResumo({ icone, titulo, quantidade, status }) { return <div className="linha-resumo"><span className={`icone-status ${status}`}><Icon nome={icone} /></span><b>{titulo}</b><strong>{quantidade}</strong><Estado status={status.toUpperCase()} /></div>; }

function Pessoas({ pessoas, recarregar, setErro }) {
  const [edicao, setEdicao] = useState(null);
  const [salvando, setSalvando] = useState(false);
  async function remover(pessoa) { if (!confirm(`Excluir ${pessoa.nome}?`)) return; try { await pessoasApi.excluir(pessoa.id); await recarregar(); } catch (e) { setErro(e.message); } }
  async function salvar(evento) { evento.preventDefault(); const form = new FormData(evento.currentTarget); setSalvando(true); setErro(''); try { const dados = { nome: form.get('nome'), apelido: form.get('apelido') }; const pessoa = edicao?.id ? await pessoasApi.atualizar(edicao.id, dados) : await pessoasApi.criar(dados); const foto = form.get('foto'); if (foto?.size) await pessoasApi.enviarFoto(pessoa.id, foto); await recarregar(); setEdicao(null); } catch (e) { setErro(e.message); } finally { setSalvando(false); } }
  return <><div className="acao"><button className="primario" onClick={() => setEdicao({})}>+ Nova pessoa</button></div>{pessoas.length === 0 ? <Vazio>Nenhuma pessoa cadastrada.</Vazio> : <div className="grade-pessoas">{pessoas.map((pessoa) => <article className="pessoa" key={pessoa.id}><Avatar pessoa={pessoa} /><div><h3>{pessoa.nome}</h3><p>{pessoa.apelido}</p></div><div className="acoes"><button onClick={() => setEdicao(pessoa)}>Editar</button><button className="perigo" onClick={() => remover(pessoa)}>Excluir</button></div></article>)}</div>}{edicao && <Modal titulo={edicao.id ? 'Editar pessoa' : 'Cadastrar pessoa'} fechar={() => setEdicao(null)}><form onSubmit={salvar}><Campo nome="nome" titulo="Nome" padrao={edicao.nome} minLength="5" required /><Campo nome="apelido" titulo="Apelido" padrao={edicao.apelido} minLength="4" required /><label>Foto (JPG, PNG ou WEBP; até 5 MB)<input name="foto" type="file" accept="image/jpeg,image/png,image/webp" /></label><button className="primario" disabled={salvando}>{salvando ? 'Salvando…' : 'Salvar pessoa'}</button></form></Modal>}</>;
}
function Campo({ nome, titulo, padrao, ...props }) { return <label>{titulo}<input name={nome} defaultValue={padrao || ''} {...props} /></label>; }
function Modal({ titulo, fechar, children }) { return <div className="fundo-modal" role="dialog" aria-modal="true"><section className="modal"><button className="fechar" onClick={fechar} aria-label="Fechar">×</button><h2>{titulo}</h2>{children}</section></div>; }

function Ciclos({ pessoas, ciclos, ciclo, cicloId, selecionarCiclo, recarregar, recarregarAgendamentos, setErro }) {
  const [novo, setNovo] = useState(false); const [participantes, setParticipantes] = useState([]); const [pessoaId, setPessoaId] = useState(''); const [carregandoParticipantes, setCarregandoParticipantes] = useState(false);
  const carregarParticipantes = useCallback(async () => { if (!cicloId) { setParticipantes([]); return; } setCarregandoParticipantes(true); try { setParticipantes(await ciclosApi.participantes(cicloId)); } catch (e) { setErro(e.message); } finally { setCarregandoParticipantes(false); } }, [cicloId, setErro]);
  useEffect(() => { carregarParticipantes(); }, [carregarParticipantes]);
  async function criar(evento) { evento.preventDefault(); const form = new FormData(evento.currentTarget); try { const criado = await ciclosApi.criar(Object.fromEntries(form)); await recarregar(); selecionarCiclo(criado.id); setNovo(false); } catch (e) { setErro(e.message); } }
  async function adicionar() { if (!pessoaId) return; try { await ciclosApi.adicionarParticipante(cicloId, pessoaId); setPessoaId(''); await carregarParticipantes(); } catch (e) { setErro(e.message); } }
  async function gerar() { if (!confirm('Gerar os agendamentos deste ciclo? Esta operação só pode ser feita uma vez.')) return; try { await agendamentosApi.gerar(cicloId); await recarregarAgendamentos(); } catch (e) { setErro(e.message); } }
  return <><div className="acao"><button className="primario" onClick={() => setNovo(true)}>+ Novo ciclo</button></div>{ciclos.length === 0 ? <Vazio>Nenhum ciclo cadastrado.</Vazio> : <div className="layout-ciclos"><section className="lista-ciclos">{ciclos.map((item) => <button className={item.id === cicloId ? 'selecionado' : ''} key={item.id} onClick={() => selecionarCiclo(item.id)}><b>{item.nome}</b><span>{item.ativo ? 'Ativo' : 'Inativo'} · começa {dataPt(item.dataInicio)}</span></button>)}</section>{ciclo && <section className="detalhe"><h2>{ciclo.nome}</h2><p>De {dataPt(ciclo.dataInicio)} a {dataPt(ciclo.dataFim)}. Primeiro pagamento: {dataPt(ciclo.primeiraSexta)}.</p><p>Primeiro responsável: <b>{ciclo.primeiroResponsavelNome} ({ciclo.primeiroResponsavelApelido})</b></p><h3>Participantes em ordem</h3>{carregandoParticipantes ? <p>Carregando…</p> : participantes.length === 0 ? <Vazio>Adicione participantes antes de gerar os agendamentos.</Vazio> : <ol>{participantes.map((p) => <li key={p.id}><b>{p.ordem}.</b> {p.pessoanome} <span>({p.pessoaapelido})</span></li>)}</ol>}<div className="linha"><select value={pessoaId} onChange={(e) => setPessoaId(e.target.value)}><option value="">Adicionar participante…</option>{pessoas.map((p) => <option key={p.id} value={p.id}>{p.nome} ({p.apelido})</option>)}</select><button onClick={adicionar} disabled={!pessoaId}>Adicionar</button></div><button className="primario" onClick={gerar}>Gerar agendamentos</button></section>}</div>}{novo && <Modal titulo="Criar ciclo" fechar={() => setNovo(false)}><form onSubmit={criar}><Campo nome="nome" titulo="Nome do ciclo" required /><Campo nome="dataInicio" titulo="Data de início" type="date" required /><Campo nome="primeiraSexta" titulo="Primeira sexta-feira" type="date" required /><label>Primeiro responsável<select name="primeiroResponsavelId" required defaultValue=""><option value="" disabled>Selecione uma pessoa</option>{pessoas.map((p) => <option key={p.id} value={p.id}>{p.nome} ({p.apelido})</option>)}</select></label><button className="primario">Criar ciclo</button></form></Modal>}</>;
}

function Calendario({ ciclo, agendamentos, recarregarAgendamentos, setErro }) {
  const [referencia, setReferencia] = useState(() => new Date()); const [detalhe, setDetalhe] = useState(null);
  const ano = referencia.getFullYear(), mes = referencia.getMonth(); const primeiroDia = new Date(ano, mes, 1).getDay(); const total = new Date(ano, mes + 1, 0).getDate();
  const porData = useMemo(() => agendamentos.reduce((mapa, item) => { (mapa[item.data] ||= []).push(item); return mapa; }, {}), [agendamentos]);
  async function atualizar(acao) { try { await acao(detalhe.id); await recarregarAgendamentos(); setDetalhe(null); } catch (e) { setErro(e.message); } }
  if (!ciclo) return <Vazio>Selecione ou crie um ciclo para visualizar o calendário.</Vazio>;
  const celulas = Array.from({ length: primeiroDia + total }, (_, indice) => { const dia = indice - primeiroDia + 1; if (dia < 1) return <div className="dia vazio-dia" key={`v-${indice}`} />; const data = `${ano}-${String(mes + 1).padStart(2, '0')}-${String(dia).padStart(2, '0')}`; const eventos = porData[data] || []; return <button className={`dia ${data === hoje ? 'hoje' : ''}`} key={data} onClick={() => eventos[0] && setDetalhe(eventos[0])}><time>{dia}</time>{eventos.map((evento) => <span className={`evento ${evento.status.toLowerCase()}`} key={evento.id}><b>{evento.tipo}</b>{evento.pessoaApelido && <small>{evento.pessoaApelido}</small>}</span>)}</button>; });
  return <><div className="navegacao"><button onClick={() => setReferencia(new Date(ano, mes - 1, 1))}>←</button><h2>{meses[mes]} de {ano}</h2><button onClick={() => setReferencia(new Date(ano, mes + 1, 1))}>→</button></div><div className="legenda"><Estado status="PENDENTE" /><Estado status="PAGO" /><Estado status="ADIADO" /></div><section className="calendario">{dias.map((dia) => <b className="nome-dia" key={dia}>{dia}</b>)}{celulas}</section>{detalhe && <Modal titulo={`${detalhe.tipo} · ${dataPt(detalhe.data)}`} fechar={() => setDetalhe(null)}><Estado status={detalhe.status} /><p>{detalhe.pessoaNome ? <>Responsável: <b>{detalhe.pessoaNome} ({detalhe.pessoaApelido})</b></> : 'A cota não possui responsável individual.'}</p>{detalhe.tipo === 'PAGAMENTO' && detalhe.status === 'PENDENTE' && <div className="linha"><button className="primario" onClick={() => atualizar(agendamentosApi.pagar)}>Marcar como pago</button><button onClick={() => atualizar(agendamentosApi.adiar)}>Adiar pagamento</button></div>}</Modal>}</>;
}
