const CACHE_NAME = 'library-ms-cache-v1';
const ASSETS_TO_CACHE = [
  './library.html',
  './manifest.json',
  './service-worker.js',
  './icons/icon-192.png',
  './icons/icon-512.png',
  // Add your CSS, JS, and other assets here if separated files
];

// Install event - cache all essential assets
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(ASSETS_TO_CACHE))
  );
});

// Activate event - cleanup old caches if any
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => 
      Promise.all(keys.map(key => {
        if (key !== CACHE_NAME) {
          return caches.delete(key);
        }
      }))
    )
  );
});

// Fetch event - serve cached assets first, then network fallback
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request).then(cachedResp => {
      return cachedResp || fetch(event.request);
    })
  );
});
