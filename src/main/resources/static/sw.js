// Service Worker Atualizado - Estratégia Network-First para Páginas
const CACHE_NAME = 'caronas-v2';

// Na instalação, evitamos cachear a raiz '/' para não prender redirecionamentos
self.addEventListener('install', (event) => {
    self.skipWaiting();
});

// Na ativação, limpamos os caches antigos da versão v1
self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cache) => {
                    if (cache !== CACHE_NAME) {
                        return caches.delete(cache);
                    }
                })
            );
        })
    );
    self.clients.claim();
});

// Intercepta as requisições com a estratégia ideal para páginas web
self.addEventListener('fetch', (event) => {
    const request = event.request;

    // Se for uma requisição de navegação (abrir páginas HTML / URLs do site)
    if (request.mode === 'navigate') {
        event.respondWith(
            // Tenta buscar sempre da rede primeiro (fala direto com o Spring Boot)
            fetch(request)
                .catch(() => {
                    // Se estiver offline e falhar, tenta buscar o fallback do cache se houver
                    return caches.match(request);
                })
        );
        return;
    }

    // Para outros recursos estáticos (CSS, JS, Imagens), mantemos o comportamento padrão de cache
    event.respondWith(
        caches.match(request).then((cachedResponse) => {
            return cachedResponse || fetch(request);
        })
    );
});
