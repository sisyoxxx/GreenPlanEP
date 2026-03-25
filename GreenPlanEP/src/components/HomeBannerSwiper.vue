<template>
    <div class="home-banner-swiper">
      <!-- Swiper 容器 -->
      <swiper
        :modules="[EffectCoverflow, Pagination, Autoplay]"
        :effect="'coverflow'"
        :grab-cursor="true"
        :centered-slides="true"
        :slides-per-view="'auto'"
        :coverflow-effect="{
          rotate: 0,
          stretch: 0,
          depth: 100,
          modifier: 2,
          slideShadows: true,
        }"
        :pagination="{ clickable: true }"
        :autoplay="{
          delay: 3000,
          disableOnInteraction: false,
        }"
        :loop="true"
        class="my-swiper"
      >
        <swiper-slide v-for="(slide, index) in slides" :key="index" class="custom-slide">
          <img :src="slide.image" :alt="slide.title" />
          <div class="slide-content">
            <h3>{{ slide.title }}</h3>
            <p>{{ slide.description }}</p>
            <button class="cta-btn" @click.stop="handleClick(slide.link)">
              {{ slide.buttonText }}
            </button>
          </div>
        </swiper-slide>
        
        <!-- 自定义分页器容器 -->
        <div class="swiper-pagination"></div>
      </swiper>
    </div>
  </template>
  
  <script setup>
  import { Swiper, SwiperSlide } from 'swiper/vue';
  import { EffectCoverflow, Pagination, Autoplay } from 'swiper/modules';
  import 'swiper/css';
  import 'swiper/css/effect-coverflow';
  import 'swiper/css/pagination';
  
  // 模拟数据
  const slides = [
    {
      image: 'https://img1.baidu.com/it/u=4048120260,4287662022&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500',
      title: '🌱 春季必种：向日葵种子',
      description: '阳光充足，生长迅速，适合新手种植！',
      buttonText: '立即购买',
      link: '/product/sunflower'
    },
    {
      image: 'https://img0.baidu.com/it/u=1101778822,534113491&fm=253&fmt=auto?w=1280&h=800',
      title: '📖 新手入门：3步成功发芽指南',
      description: '从选种到育苗，手把手教你轻松上手',
      buttonText: '查看教程',
      link: '/tutorial/beginner'
    },
    {
      image: 'https://img2.baidu.com/it/u=543924726,2911709898&fm=253&fmt=auto&app=120&f=JPEG?w=799&h=500',
      title: '🎁 新人专享：满50减10元',
      description: '首次下单即享优惠，还有免费种植工具包',
      buttonText: '立即领取',
      link: '/coupon/newuser'
    },
    {
      image: 'https://img2.baidu.com/it/u=1019708632,3994611850&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=664',
      tag: '🌿 绿植养护',
      title: '浇水频率全解析',
      description: '不同季节、不同植物，浇水策略大不同',
      buttonText: '查看详情'
    },
    {
      image: 'https://img1.baidu.com/it/u=2737806807,632001265&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=667',
      tag: '🎨 创意盆栽',
      title: '用废旧瓶子做迷你花园',
      description: '环保又美观，DIY你的专属小森林',
      buttonText: '看教程'
    }
  ];
  
  const handleClick = (link) => {
    console.log('跳转:', link);
    // router.push(link)
  };
</script>
  




<style scoped>
  .home-banner-swiper {
    width: 100%;
    height: 250px; /* 调整高度 */
    margin-bottom: 30px;
  }
  
  .my-swiper {
    width: 100%;
    height: 100%;
    top: 20px;
    padding-bottom: 40px !important; /* 给分页器留空间 */
  }
  
  /*轮播图卡片组*/
  .custom-slide {
    width: 60%; /* 控制卡片宽度 */
    max-width: 600px;
    border-radius: 16px;
    overflow: hidden;
    position: relative;
    box-shadow: 0 15px 50px rgba(0,0,0,0.3);
    transition: all 0.3s;
  }
  
  .custom-slide img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
  
  .slide-content {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(to top, rgba(255, 255, 255, 0.5), transparent);
    color: white;
    padding: 30px 25px 25px;
    text-align: left;
  }
  
  .slide-content h3 {
    font-size: 22px;
    margin: 0 0 8px 0;
    font-weight: 700;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  }
  
  .slide-content p {
    font-size: 14px;
    margin: 0 0 15px 0;
    opacity: 0.9;
    line-height: 1.4;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.6);
  }
  
  .cta-btn {
    background: #10b981;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 8px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: transform 0.2s;
  }
  
  .cta-btn:hover {
    background: #059669;
    transform: translateY(-2px);
  }
  
  /* 自定义分页器颜色 */
  :deep(.swiper-pagination-bullet) {
    background: white;
    opacity: 0.5;
  }
  :deep(.swiper-pagination-bullet-active) {
    background: #10b981;
    opacity: 1;
    width: 20px;
    border-radius: 5px;
  }
  </style>