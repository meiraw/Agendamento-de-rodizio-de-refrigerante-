const API_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8080').replace(/\/$/, '');

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, options);
  if (response.status === 204) return null;
  const body = await response.json().catch(() => null);
  if (!response.ok) throw new Error(body?.mensagem || body?.message || `Erro ${response.status} ao comunicar com a API.`);
  return body;
}

const json = (method, data) => ({
  method,
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(data),
});

export const fotoCompleta = (fotoUrl) => fotoUrl ? new URL(fotoUrl, `${API_URL}/`).toString() : null;

export const pessoasApi = {
  listar: () => request('/pessoas'),
  criar: (dados) => request('/pessoas', json('POST', dados)),
  atualizar: (id, dados) => request(`/pessoas/${id}`, json('PUT', dados)),
  excluir: (id) => request(`/pessoas/${id}`, { method: 'DELETE' }),
  enviarFoto: (id, arquivo) => {
    const formData = new FormData();
    formData.append('foto', arquivo);
    return request(`/pessoas/${id}/foto`, { method: 'POST', body: formData });
  },
};

export const ciclosApi = {
  listar: () => request('/ciclos'),
  buscar: (id) => request(`/ciclos/${id}`),
  criar: (dados) => request('/ciclos', json('POST', dados)),
  participantes: (id) => request(`/ciclos/${id}/participantes`),
  adicionarParticipante: (id, pessoaId) => request(`/ciclos/${id}/participantes`, json('POST', { pessoaId })),
};

export const agendamentosApi = {
  listarPorCiclo: (cicloId) => request(`/agendamentos/ciclo/${cicloId}`),
  gerar: (cicloId) => request(`/agendamentos/gerar/${cicloId}`, { method: 'POST' }),
  pagar: (id) => request(`/agendamentos/${id}/pagar`, { method: 'PUT' }),
  adiar: (id) => request(`/agendamentos/${id}/adiar`, { method: 'PUT' }),
};
